import { IEmployee } from 'app/shared/model/employee.model';

export interface IJob {
  id?: number;
  jobTitle?: string;
  minSalary?: number;
  maxSalary?: number;
  employee?: IEmployee;
}

export const defaultValue: Readonly<IJob> = {};
