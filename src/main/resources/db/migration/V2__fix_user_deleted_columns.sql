-- Ensure NULLs are replaced
update viaz_dashboard_users
SET deleted = false
where deleted is null;

-- Set column to NOT NULL with default false
alter table viaz_dashboard_users
alter column deleted set default false;

alter table viaz_dashboard_users
alter column deleted set not null;