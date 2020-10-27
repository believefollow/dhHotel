import { Moment } from 'moment';
import { ICustomer } from 'app/shared/model/customer.model';
import { IBill } from 'app/shared/model/bill.model';
import { IRoom } from 'app/shared/model/room.model';

export interface ICheckIn {
  id?: number;
  startTime?: string;
  endTime?: string;
  customers?: ICustomer[];
  bills?: IBill[];
  rooms?: IRoom[];
}

export const defaultValue: Readonly<ICheckIn> = {};
