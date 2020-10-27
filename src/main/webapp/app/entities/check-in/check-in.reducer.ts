import axios from 'axios';
import {
  ICrudSearchAction,
  parseHeaderForLinks,
  loadMoreDataWhenScrolled,
  ICrudGetAction,
  ICrudGetAllAction,
  ICrudPutAction,
  ICrudDeleteAction,
} from 'react-jhipster';

import { cleanEntity } from 'app/shared/util/entity-utils';
import { REQUEST, SUCCESS, FAILURE } from 'app/shared/reducers/action-type.util';

import { ICheckIn, defaultValue } from 'app/shared/model/check-in.model';

export const ACTION_TYPES = {
  SEARCH_CHECKINS: 'checkIn/SEARCH_CHECKINS',
  FETCH_CHECKIN_LIST: 'checkIn/FETCH_CHECKIN_LIST',
  FETCH_CHECKIN: 'checkIn/FETCH_CHECKIN',
  CREATE_CHECKIN: 'checkIn/CREATE_CHECKIN',
  UPDATE_CHECKIN: 'checkIn/UPDATE_CHECKIN',
  DELETE_CHECKIN: 'checkIn/DELETE_CHECKIN',
  RESET: 'checkIn/RESET',
};

const initialState = {
  loading: false,
  errorMessage: null,
  entities: [] as ReadonlyArray<ICheckIn>,
  entity: defaultValue,
  links: { next: 0 },
  updating: false,
  totalItems: 0,
  updateSuccess: false,
};

export type CheckInState = Readonly<typeof initialState>;

// Reducer

export default (state: CheckInState = initialState, action): CheckInState => {
  switch (action.type) {
    case REQUEST(ACTION_TYPES.SEARCH_CHECKINS):
    case REQUEST(ACTION_TYPES.FETCH_CHECKIN_LIST):
    case REQUEST(ACTION_TYPES.FETCH_CHECKIN):
      return {
        ...state,
        errorMessage: null,
        updateSuccess: false,
        loading: true,
      };
    case REQUEST(ACTION_TYPES.CREATE_CHECKIN):
    case REQUEST(ACTION_TYPES.UPDATE_CHECKIN):
    case REQUEST(ACTION_TYPES.DELETE_CHECKIN):
      return {
        ...state,
        errorMessage: null,
        updateSuccess: false,
        updating: true,
      };
    case FAILURE(ACTION_TYPES.SEARCH_CHECKINS):
    case FAILURE(ACTION_TYPES.FETCH_CHECKIN_LIST):
    case FAILURE(ACTION_TYPES.FETCH_CHECKIN):
    case FAILURE(ACTION_TYPES.CREATE_CHECKIN):
    case FAILURE(ACTION_TYPES.UPDATE_CHECKIN):
    case FAILURE(ACTION_TYPES.DELETE_CHECKIN):
      return {
        ...state,
        loading: false,
        updating: false,
        updateSuccess: false,
        errorMessage: action.payload,
      };
    case SUCCESS(ACTION_TYPES.SEARCH_CHECKINS):
    case SUCCESS(ACTION_TYPES.FETCH_CHECKIN_LIST): {
      const links = parseHeaderForLinks(action.payload.headers.link);

      return {
        ...state,
        loading: false,
        links,
        entities: loadMoreDataWhenScrolled(state.entities, action.payload.data, links),
        totalItems: parseInt(action.payload.headers['x-total-count'], 10),
      };
    }
    case SUCCESS(ACTION_TYPES.FETCH_CHECKIN):
      return {
        ...state,
        loading: false,
        entity: action.payload.data,
      };
    case SUCCESS(ACTION_TYPES.CREATE_CHECKIN):
    case SUCCESS(ACTION_TYPES.UPDATE_CHECKIN):
      return {
        ...state,
        updating: false,
        updateSuccess: true,
        entity: action.payload.data,
      };
    case SUCCESS(ACTION_TYPES.DELETE_CHECKIN):
      return {
        ...state,
        updating: false,
        updateSuccess: true,
        entity: {},
      };
    case ACTION_TYPES.RESET:
      return {
        ...initialState,
      };
    default:
      return state;
  }
};

const apiUrl = 'api/check-ins';
const apiSearchUrl = 'api/_search/check-ins';

// Actions

export const getSearchEntities: ICrudSearchAction<ICheckIn> = (query, page, size, sort) => ({
  type: ACTION_TYPES.SEARCH_CHECKINS,
  payload: axios.get<ICheckIn>(`${apiSearchUrl}?query=${query}${sort ? `&page=${page}&size=${size}&sort=${sort}` : ''}`),
});

export const getEntities: ICrudGetAllAction<ICheckIn> = (page, size, sort) => {
  const requestUrl = `${apiUrl}${sort ? `?page=${page}&size=${size}&sort=${sort}` : ''}`;
  return {
    type: ACTION_TYPES.FETCH_CHECKIN_LIST,
    payload: axios.get<ICheckIn>(`${requestUrl}${sort ? '&' : '?'}cacheBuster=${new Date().getTime()}`),
  };
};

export const getEntity: ICrudGetAction<ICheckIn> = id => {
  const requestUrl = `${apiUrl}/${id}`;
  return {
    type: ACTION_TYPES.FETCH_CHECKIN,
    payload: axios.get<ICheckIn>(requestUrl),
  };
};

export const createEntity: ICrudPutAction<ICheckIn> = entity => async dispatch => {
  const result = await dispatch({
    type: ACTION_TYPES.CREATE_CHECKIN,
    payload: axios.post(apiUrl, cleanEntity(entity)),
  });
  return result;
};

export const updateEntity: ICrudPutAction<ICheckIn> = entity => async dispatch => {
  const result = await dispatch({
    type: ACTION_TYPES.UPDATE_CHECKIN,
    payload: axios.put(apiUrl, cleanEntity(entity)),
  });
  return result;
};

export const deleteEntity: ICrudDeleteAction<ICheckIn> = id => async dispatch => {
  const requestUrl = `${apiUrl}/${id}`;
  const result = await dispatch({
    type: ACTION_TYPES.DELETE_CHECKIN,
    payload: axios.delete(requestUrl),
  });
  return result;
};

export const reset = () => ({
  type: ACTION_TYPES.RESET,
});
