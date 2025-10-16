ALTER TABLE copy
add column branch_id bigint not null;

alter table if exists copy
    add constraint FK_copy_2_branch foreign key (branch_id) references branch;
