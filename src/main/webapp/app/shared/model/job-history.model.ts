import { Moment } from 'moment';
import { IJob } from 'app/shared/model/job.model';
import { IEmployee } from 'app/shared/model/employee.model';

export interface IJobHistory {
  id?: number;
  startDate?: string;
  endDate?: string;
  job?: IJob;
  employee?: IEmployee;
}

export const defaultValue: Readonly<IJobHistory> = {};
