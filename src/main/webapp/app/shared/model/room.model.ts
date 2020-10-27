import { ICheckIn } from 'app/shared/model/check-in.model';
import { RoomType } from 'app/shared/model/enumerations/room-type.model';
import { PriceType } from 'app/shared/model/enumerations/price-type.model';

export interface IRoom {
  id?: number;
  roomType?: RoomType;
  priceType?: PriceType;
  checkIn?: ICheckIn;
}

export const defaultValue: Readonly<IRoom> = {};
