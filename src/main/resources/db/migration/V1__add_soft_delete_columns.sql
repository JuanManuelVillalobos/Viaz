ALTER TABLE shipments
    ADD COLUMN deleted            BOOLEAN DEFAULT FALSE,
    ADD COLUMN deleted_at         TIMESTAMP,
    ADD COLUMN deleted_by_user_id BIGINT;

ALTER TABLE shipments
    ADD CONSTRAINT fk_deleted_by_user
        FOREIGN KEY (deleted_by_user_id)
            REFERENCES public.viaz_dashboard_users (user_id);
