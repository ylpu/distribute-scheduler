create table tbl_job(id int not null auto_increment PRIMARY KEY,job_id varchar(64) not null,job_name varchar(128) not null,command varchar(1024) not null,job_type tinyint(1) not null,pool_path varchar(64),retry_times int,cron_expression varchar(64),
strategy varchar(32),classpath varchar(1024),command_parameters varchar(1024),execute_parameters varchar(1024),create_time timestamp DEFAULT CURRENT_TIMESTAMP,update_time timestamp DEFAULT CURRENT_TIMESTAMP) ENGINE=InnoDB DEFAULT CHARSET=utf8 DEFAULT COLLATE=utf8_general_ci;


create table tbl_work_flow(id int not null auto_increment PRIMARY KEY,plan_id int not null,status tinyint(1) not null,start_date timestamp DEFAULT CURRENT_TIMESTAMP,end_date timestamp DEFAULT CURRENT_TIMESTAMP,create_time timestamp DEFAULT CURRENT_TIMESTAMP,update_time timestamp DEFAULT CURRENT_TIMESTAMP)
ENGINE=InnoDB DEFAULT CHARSET=utf8 DEFAULT COLLATE=utf8_general_ci;

create table tbl_work_plan(id int not null auto_increment PRIMARY KEY,plan_name varchar(64) not null,cron varchar(64),job_plan_file longtext,create_time timestamp DEFAULT CURRENT_TIMESTAMP,update_time timestamp DEFAULT CURRENT_TIMESTAMP)
ENGINE=InnoDB DEFAULT CHARSET=utf8 DEFAULT COLLATE=utf8_general_ci;

create table tbl_task(id int not null auto_increment PRIMARY KEY,task_id varchar(64) not null,plan_instance_id int,job_id varchar(64),failed_times int,running_host varchar(64),lastFailedHost varchar(64),task_status tinyint(1),
error_output_url varchar(512),std_output_url varchar(512),start_time timestamp DEFAULT CURRENT_TIMESTAMP,end_time timestamp DEFAULT CURRENT_TIMESTAMP,elapse_time bigint,
create_time timestamp DEFAULT CURRENT_TIMESTAMP,update_time timestamp DEFAULT CURRENT_TIMESTAMP) ENGINE=InnoDB DEFAULT CHARSET=utf8 DEFAULT COLLATE=utf8_general_ci;

