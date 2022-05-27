package qna.domain;

import static javax.persistence.GenerationType.IDENTITY;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import qna.CannotDeleteException;

@Entity
public class Question extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;
    @Column(length = 100, nullable = false)
    private String title;
    @Lob
    private String contents;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "WRITER_ID", foreignKey = @ForeignKey(name = "fk_question_writer"))
    private User writer;
    @Column(nullable = false)
    private boolean deleted = false;

    @OneToMany(mappedBy = "question")
    private List<Answer> answers = new LinkedList<>();

    protected Question() {

    }

    public Question(String title, String contents) {
        this(null, title, contents);
    }

    public Question(Long id, String title, String contents) {
        this.id = id;
        this.title = title;
        this.contents = contents;
    }

    public Question writeBy(User writer) {
        this.writer = writer;
        return this;
    }

    private boolean isOwner(User writer) {
        return this.writer == writer;
    }

    public void addAnswer(Answer answer) {
        answer.toQuestion(this);
    }

    public Long getId() {
        return id;
    }

    public User getWriter() {
        return writer;
    }

    public boolean isDeleted() {
        return deleted;
    }


    public List<Answer> getAnswers() {
        return answers;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Question{" +
            "id=" + id +
            ", title='" + title + '\'' +
            ", contents='" + contents + '\'' +
            ", writerId=" + writer.getId() +
            ", deleted=" + deleted +
            '}';
    }

    public List<DeleteHistory> delete(User user) {
        if (!this.isOwner(user)) {
            throw new CannotDeleteException("질문을 삭제할 권한이 없습니다.");
        }
        List<DeleteHistory> deleteHistories = new LinkedList<>();
        try{
            this.deleted = true;
            deleteHistories.add(makeDeleteHistory());
            deleteAnswer(deleteHistories);
        }catch(CannotDeleteException e){
            rollbackDelete(deleteHistories);
            throw new CannotDeleteException(e.getMessage());
        }
        return deleteHistories;
    }

    private void rollbackDelete(List<DeleteHistory> deleteHistories) {
        this.deleted = false;
        List<Long> answerIds = deleteHistories.stream()
            .filter(deleteHistory -> deleteHistory.getContentType() == ContentType.ANSWER)
            .map(DeleteHistory::getId)
            .collect(Collectors.toList());
        for (Answer answer : answers) {
            if (answerIds.contains(answer.getId())) {
                answer.rollbackDelete();
            }
        }
    }

    private void deleteAnswer(List<DeleteHistory> deleteHistories) {
        for (Answer answer : answers) {
            deleteHistories.add(answer.delete(this.writer));
        }
    }

    public DeleteHistory makeDeleteHistory() {
        return new DeleteHistory(ContentType.QUESTION, this.getId(), this.writer,
            LocalDateTime.now());
    }

}
