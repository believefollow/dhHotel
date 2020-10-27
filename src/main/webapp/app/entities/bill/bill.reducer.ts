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

import { IBill, defaultValue } from 'app/shared/model/bill.model';

export const ACTION_TYPES = {
  SEARCH_BILLS: 'bill/SEARCH_BILLS',
  FETCH_BILL_LIST: 'bill/FETCH_BILL_LIST',
  FETCH_BILL: 'bill/FETCH_BILL',
  CREATE_BILL: 'bill/CREATE_BILL',
  UPDATE_BILL: 'bill/UPDATE_BILL',
  DELETE_BILL: 'bill/DELETE_BILL',
  RESET: 'bill/RESET',
};

const initialState = {
  loading: false,
  errorMessage: null,
  entities: [] as ReadonlyArray<IBill>,
  entity: defaultValue,
  links: { next: 0 },
  updating: false,
  totalItems: 0,
  updateSuccess: false,
};

export type BillState = Readonly<typeof initialState>;

// Reducer

export default (state: BillState = initialState, action): BillState => {
  switch (action.type) {
    case REQUEST(ACTION_TYPES.SEARCH_BILLS):
    case REQUEST(ACTION_TYPES.FETCH_BILL_LIST):
    case REQUEST(ACTION_TYPES.FETCH_BILL):
      return {
        ...state,
        errorMessage: null,
        updateSuccess: false,
        loading: true,
      };
    case REQUEST(ACTION_TYPES.CREATE_BILL):
    case REQUEST(ACTION_TYPES.UPDATE_BILL):
    case REQUEST(ACTION_TYPES.DELETE_BILL):
      return {
        ...state,
        errorMessage: null,
        updateSuccess: false,
        updating: true,
      };
    case FAILURE(ACTION_TYPES.SEARCH_BILLS):
    case FAILURE(ACTION_TYPES.FETCH_BILL_LIST):
    case FAILURE(ACTION_TYPES.FETCH_BILL):
    case FAILURE(ACTION_TYPES.CREATE_BILL):
    case FAILURE(ACTION_TYPES.UPDATE_BILL):
    case FAILURE(ACTION_TYPES.DELETE_BILL):
      return {
        ...state,
        loading: false,
        updating: false,
        updateSuccess: false,
        errorMessage: action.payload,
      };
    case SUCCESS(ACTION_TYPES.SEARCH_BILLS):
    case SUCCESS(ACTION_TYPES.FETCH_BILL_LIST): {
      const links = parseHeaderForLinks(action.payload.headers.link);

      return {
        ...state,
        loading: false,
        links,
        entities: loadMoreDataWhenScrolled(state.entities, action.payload.data, links),
        totalItems: parseInt(action.payload.headers['x-total-count'], 10),
      };
    }
    case SUCCESS(ACTION_TYPES.FETCH_BILL):
      return {
        ...state,
        loading: false,
        entity: action.payload.data,
      };
    case SUCCESS(ACTION_TYPES.CREATE_BILL):
    case SUCCESS(ACTION_TYPES.UPDATE_BILL):
      return {
        ...state,
        updating: false,
        updateSuccess: true,
        entity: action.payload.data,
      };
    case SUCCESS(ACTION_TYPES.DELETE_BILL):
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

const apiUrl = 'api/bills';
const apiSearchUrl = 'api/_search/bills';

// Actions

export const getSearchEntities: ICrudSearchAction<IBill> = (query, page, size, sort) => ({
  type: ACTION_TYPES.SEARCH_BILLS,
  payload: axios.get<IBill>(`${apiSearchUrl}?query=${query}${sort ? `&page=${page}&size=${size}&sort=${sort}` : ''}`),
});

export const getEntities: ICrudGetAllAction<IBill> = (page, size, sort) => {
  const requestUrl = `${apiUrl}${sort ? `?page=${page}&size=${size}&sort=${sort}` : ''}`;
  return {
    type: ACTION_TYPES.FETCH_BILL_LIST,
    payload: axios.get<IBill>(`${requestUrl}${sort ? '&' : '?'}cacheBuster=${new Date().getTime()}`),
  };
};

export const getEntity: ICrudGetAction<IBill> = id => {
  const requestUrl = `${apiUrl}/${id}`;
  return {
    type: ACTION_TYPES.FETCH_BILL,
    payload: axios.get<IBill>(requestUrl),
  };
};

export const createEntity: ICrudPutAction<IBill> = entity => async dispatch => {
  const result = await dispatch({
    type: ACTION_TYPES.CREATE_BILL,
    payload: axios.post(apiUrl, cleanEntity(entity)),
  });
  return result;
};

export const updateEntity: ICrudPutAction<IBill> = entity => async dispatch => {
  const result = await dispatch({
    type: ACTION_TYPES.UPDATE_BILL,
    payload: axios.put(apiUrl, cleanEntity(entity)),
  });
  return result;
};

export const deleteEntity: ICrudDeleteAction<IBill> = id => async dispatch => {
  const requestUrl = `${apiUrl}/${id}`;
  const result = await dispatch({
    type: ACTION_TYPES.DELETE_BILL,
    payload: axios.delete(requestUrl),
  });
  return result;
};

export const reset = () => ({
  type: ACTION_TYPES.RESET,
});
