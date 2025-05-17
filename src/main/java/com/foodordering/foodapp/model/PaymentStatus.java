package com.foodordering.foodapp.model;

public enum PaymentStatus {

    PENDING {
        @Override
        public boolean canTransitionTo(PaymentStatus newStatus) {
            return newStatus == PROCESSING ||
                    newStatus == FAILED ||
                    newStatus == CANCELLED;
        }
    },

    PROCESSING {
        @Override
        public boolean canTransitionTo(PaymentStatus newStatus) {
            return newStatus == COMPLETED ||
                    newStatus == FAILED ||
                    newStatus == CANCELLED;
        }
    },

    COMPLETED {
        @Override
        public boolean canTransitionTo(PaymentStatus newStatus) {
            return newStatus == REFUNDED ||
                    newStatus == PARTIALLY_REFUNDED;
        }
    },

    FAILED {
        @Override
        public boolean canTransitionTo(PaymentStatus newStatus) {
            return newStatus == RETRYING;
        }
    },

    CANCELLED {
        @Override
        public boolean canTransitionTo(PaymentStatus newStatus) {
            return false;
        }
    },

    RETRYING {
        @Override
        public boolean canTransitionTo(PaymentStatus newStatus) {
            return newStatus == PROCESSING ||
                    newStatus == FAILED ||
                    newStatus == CANCELLED;
        }
    },

    REFUNDED {
        @Override
        public boolean canTransitionTo(PaymentStatus newStatus) {
            return false;
        }
    },

    PARTIALLY_REFUNDED {
        @Override
        public boolean canTransitionTo(PaymentStatus newStatus) {
            return newStatus == REFUNDED;
        }
    },

    ON_HOLD {
        @Override
        public boolean canTransitionTo(PaymentStatus newStatus) {
            return newStatus == PROCESSING ||
                    newStatus == FAILED ||
                    newStatus == CANCELLED;
        }
    };

    public abstract boolean canTransitionTo(PaymentStatus newStatus);

    public static void validateTransition(PaymentStatus current, PaymentStatus newStatus) {
        if (!current.canTransitionTo(newStatus)) {
            throw new IllegalStateException(
                    String.format("Invalid payment status transition from %s to %s", current, newStatus)
            );
        }
    }

    public boolean isTerminal() {
        // Terminal states are those that can't transition to any other state
        return this == CANCELLED || this == REFUNDED;
    }

    public boolean isSuccessful() {
        return this == COMPLETED ||
                this == REFUNDED ||
                this == PARTIALLY_REFUNDED;
    }

    public boolean isRefundable() {
        return this == COMPLETED;
    }
}