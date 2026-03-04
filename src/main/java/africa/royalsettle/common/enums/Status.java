package africa.royalsettle.common.enums;

import lombok.Getter;

@Getter
public enum Status {
    // do not reorder values
    INACTIVE(0, "inactive"),
    ACTIVE(1, "active"),
    PENDING(2, "pending"),
    REJECTED(3, "rejected"),
    HIBERNATING(5, "hibernating"),
    DECLINED(6, "declined"),
    APPROVED(7, "approved"),
    DISBURSED(8, "disbursed"),
    COMPLETED(9, "completed"),
    BLOCKED(10, "blocked"),
    TERMINATED(11, "terminated"),
    DEFAULT(12, "default"),
    RECOVERY(13, "recovery"),
    ACCEPTED(14, "accepted"),
    CANCELLED(15, "cancelled"),
    MIGRATED(16, "migrated"),
    CONSOLIDATED(17, "consolidated"),
    DEACTIVATED(18, "deactivated"),
    PENDING_DEACTIVATION(19, "pending deactivation"),
    SENT(20, "sent"),
    FAILED(21, "failed"),
    SUSPENDED(22, "suspended"),
    PROCESSED(23, "processed"),
    PENDING_KEY_DOWNLOAD(24, "pending key download");

    private final int code;

    private final String description;

    Status(int code, String description) {
        this.code = code;
        this.description = description;
    }
}
