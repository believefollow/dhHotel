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

import { IVip, defaultValue } from 'app/shared/model/vip.model';

export const ACTION_TYPES = {
  SEARCH_VIPS: 'vip/SEARCH_VIPS',
  FETCH_VIP_LIST: 'vip/FETCH_VIP_LIST',
  FETCH_VIP: 'vip/FETCH_VIP',
  CREATE_VIP: 'vip/CREATE_VIP',
  UPDATE_VIP: 'vip/UPDATE_VIP',
  DELETE_VIP: 'vip/DELETE_VIP',
  RESET: 'vip/RESET',
};

const initialState = {
  loading: false,
  errorMessage: null,
  entities: [] as ReadonlyArray<IVip>,
  entity: defaultValue,
  links: { next: 0 },
  updating: false,
  totalItems: 0,
  updateSuccess: false,
};

export type VipState = Readonly<typeof initialState>;

// Reducer

export default (state: VipState = initialState, action): VipState => {
  switch (action.type) {
    case REQUEST(ACTION_TYPES.SEARCH_VIPS):
    case REQUEST(ACTION_TYPES.FETCH_VIP_LIST):
    case REQUEST(ACTION_TYPES.FETCH_VIP):
      return {
        ...state,
        errorMessage: null,
        updateSuccess: false,
        loading: true,
      };
    case REQUEST(ACTION_TYPES.CREATE_VIP):
    case REQUEST(ACTION_TYPES.UPDATE_VIP):
    case REQUEST(ACTION_TYPES.DELETE_VIP):
      return {
        ...state,
        errorMessage: null,
        updateSuccess: false,
        updating: true,
      };
    case FAILURE(ACTION_TYPES.SEARCH_VIPS):
    case FAILURE(ACTION_TYPES.FETCH_VIP_LIST):
    case FAILURE(ACTION_TYPES.FETCH_VIP):
    case FAILURE(ACTION_TYPES.CREATE_VIP):
    case FAILURE(ACTION_TYPES.UPDATE_VIP):
    case FAILURE(ACTION_TYPES.DELETE_VIP):
      return {
        ...state,
        loading: false,
        updating: false,
        updateSuccess: false,
        errorMessage: action.payload,
      };
    case SUCCESS(ACTION_TYPES.SEARCH_VIPS):
    case SUCCESS(ACTION_TYPES.FETCH_VIP_LIST): {
      const links = parseHeaderForLinks(action.payload.headers.link);

      return {
        ...state,
        loading: false,
        links,
        entities: loadMoreDataWhenScrolled(state.entities, action.payload.data, links),
        totalItems: parseInt(action.payload.headers['x-total-count'], 10),
      };
    }
    case SUCCESS(ACTION_TYPES.FETCH_VIP):
      return {
        ...state,
        loading: false,
        entity: action.payload.data,
      };
    case SUCCESS(ACTION_TYPES.CREATE_VIP):
    case SUCCESS(ACTION_TYPES.UPDATE_VIP):
      return {
        ...state,
        updating: false,
        updateSuccess: true,
        entity: action.payload.data,
      };
    case SUCCESS(ACTION_TYPES.DELETE_VIP):
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

const apiUrl = 'api/vips';
const apiSearchUrl = 'api/_search/vips';

// Actions

export const getSearchEntities: ICrudSearchAction<IVip> = (query, page, size, sort) => ({
  type: ACTION_TYPES.SEARCH_VIPS,
  payload: axios.get<IVip>(`${apiSearchUrl}?query=${query}${sort ? `&page=${page}&size=${size}&sort=${sort}` : ''}`),
});

export const getEntities: ICrudGetAllAction<IVip> = (page, size, sort) => {
  const requestUrl = `${apiUrl}${sort ? `?page=${page}&size=${size}&sort=${sort}` : ''}`;
  return {
    type: ACTION_TYPES.FETCH_VIP_LIST,
    payload: axios.get<IVip>(`${requestUrl}${sort ? '&' : '?'}cacheBuster=${new Date().getTime()}`),
  };
};

export const getEntity: ICrudGetAction<IVip> = id => {
  const requestUrl = `${apiUrl}/${id}`;
  return {
    type: ACTION_TYPES.FETCH_VIP,
    payload: axios.get<IVip>(requestUrl),
  };
};

export const createEntity: ICrudPutAction<IVip> = entity => async dispatch => {
  const result = await dispatch({
    type: ACTION_TYPES.CREATE_VIP,
    payload: axios.post(apiUrl, cleanEntity(entity)),
  });
  return result;
};

export const updateEntity: ICrudPutAction<IVip> = entity => async dispatch => {
  const result = await dispatch({
    type: ACTION_TYPES.UPDATE_VIP,
    payload: axios.put(apiUrl, cleanEntity(entity)),
  });
  return result;
};

export const deleteEntity: ICrudDeleteAction<IVip> = id => async dispatch => {
  const requestUrl = `${apiUrl}/${id}`;
  const result = await dispatch({
    type: ACTION_TYPES.DELETE_VIP,
    payload: axios.delete(requestUrl),
  });
  return result;
};

export const reset = () => ({
  type: ACTION_TYPES.RESET,
});
