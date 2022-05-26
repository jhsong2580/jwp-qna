package qna.domain;

import static javax.persistence.EnumType.STRING;
import static javax.persistence.GenerationType.IDENTITY;

import java.time.LocalDateTime;
import java.util.Objects;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class DeleteHistory {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;
    @Enumerated(STRING)
    private ContentType contentType;
    private Long contentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DELETE_BY_ID", foreignKey = @ForeignKey(name = "fk_delete_history_to_user"))
    private User deletedById;


    private LocalDateTime createDate = LocalDateTime.now();

    public DeleteHistory(ContentType contentType, Long contentId, User deletedById) {
        this.contentType = contentType;
        this.contentId = contentId;
        this.deletedById = deletedById;
    }

    public DeleteHistory(ContentType contentType, Long contentId, User deletedById,
        LocalDateTime createDate) {
        this.contentType = contentType;
        this.contentId = contentId;
        this.deletedById = deletedById;
        this.createDate = createDate;
    }

    protected DeleteHistory() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        DeleteHistory that = (DeleteHistory) o;
        return Objects.equals(id, that.id) &&
            contentType == that.contentType &&
            Objects.equals(contentId, that.contentId) &&
            Objects.equals(deletedById, that.deletedById);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, contentType, contentId, deletedById);
    }

    @Override
    public String toString() {
        return "DeleteHistory{" +
            "id=" + id +
            ", contentType=" + contentType +
            ", contentId=" + contentId +
            ", deletedById=" + deletedById +
            ", createDate=" + createDate +
            '}';
    }

    public Long getId() {
        return id;
    }

}
