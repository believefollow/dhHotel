import { ICheckIn } from 'app/shared/model/check-in.model';
import { Sex } from 'app/shared/model/enumerations/sex.model';

export interface ICustomer {
  id?: number;
  name?: string;
  idCard?: string;
  sex?: Sex;
  address?: string;
  nation?: string;
  checkIn?: ICheckIn;
}

export const defaultValue: Readonly<ICustomer> = {};
