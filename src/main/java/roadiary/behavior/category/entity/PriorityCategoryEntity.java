package roadiary.behavior.category.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter @Setter
public class PriorityCategoryEntity {

    private long userId;
    private int priorityIdx;
    private long behaviorCategoryId;

}
