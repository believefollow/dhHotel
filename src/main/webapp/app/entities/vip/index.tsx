import React from 'react';
import { Switch } from 'react-router-dom';

import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import Vip from './vip';
import VipDetail from './vip-detail';
import VipUpdate from './vip-update';
import VipDeleteDialog from './vip-delete-dialog';

const Routes = ({ match }) => (
  <>
    <Switch>
      <ErrorBoundaryRoute exact path={`${match.url}/new`} component={VipUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id/edit`} component={VipUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id`} component={VipDetail} />
      <ErrorBoundaryRoute path={match.url} component={Vip} />
    </Switch>
    <ErrorBoundaryRoute exact path={`${match.url}/:id/delete`} component={VipDeleteDialog} />
  </>
);

export default Routes;
