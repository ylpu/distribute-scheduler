create table tbl_job(id int not null auto_increment PRIMARY KEY,job_id varchar(64) not null,job_name varchar(128) not null,job_desc varchar(512) not null,job_type varchar(20) not null,pool_path varchar(64),retry_times int,job_strategy varchar(32),cron_expression varchar(64),
command varchar(1024) not null,alert_email varchar(64),owner varchar(64),resource_parameters varchar(128),create_time timestamp DEFAULT CURRENT_TIMESTAMP,update_time timestamp DEFAULT CURRENT_TIMESTAMP) ENGINE=InnoDB DEFAULT CHARSET=utf8 DEFAULT COLLATE=utf8_general_ci;

create table tbl_work_flow(id int not null auto_increment PRIMARY KEY,plan_id int not null,status tinyint(1) not null,start_date timestamp DEFAULT CURRENT_TIMESTAMP,end_date timestamp DEFAULT CURRENT_TIMESTAMP,create_time timestamp DEFAULT CURRENT_TIMESTAMP,update_time timestamp DEFAULT CURRENT_TIMESTAMP)
ENGINE=InnoDB DEFAULT CHARSET=utf8 DEFAULT COLLATE=utf8_general_ci;

create table tbl_work_plan(id int not null auto_increment PRIMARY KEY,plan_name varchar(64) not null,job_plan_file longtext,create_time timestamp DEFAULT CURRENT_TIMESTAMP,update_time timestamp DEFAULT CURRENT_TIMESTAMP)
ENGINE=InnoDB DEFAULT CHARSET=utf8 DEFAULT COLLATE=utf8_general_ci;

create table tbl_task(id int not null auto_increment PRIMARY KEY,task_id varchar(64) not null,flow_id int,job_id varchar(64),failed_times int,running_host varchar(64),lastFailedHost varchar(64),task_status varchar(32),
error_output_url varchar(512),std_output_url varchar(512),start_time timestamp DEFAULT CURRENT_TIMESTAMP,end_time timestamp DEFAULT CURRENT_TIMESTAMP,elapse_time bigint) ENGINE=InnoDB DEFAULT CHARSET=utf8 DEFAULT COLLATE=utf8_general_ci;