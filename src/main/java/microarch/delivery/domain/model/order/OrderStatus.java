package microarch.delivery.domain.model.order;

public enum OrderStatus {
    CREATED(false),
    ASSIGNED(true),
    COMPLETED(true);

    private final boolean progressStatus;

    OrderStatus(boolean progressStatus) {
        this.progressStatus = progressStatus;
    }

    public boolean isProgressStatus() {
        return progressStatus;
    }
}
