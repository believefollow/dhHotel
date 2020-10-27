import React from 'react';
import { Switch } from 'react-router-dom';

import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import CheckIn from './check-in';
import CheckInDetail from './check-in-detail';
import CheckInUpdate from './check-in-update';
import CheckInDeleteDialog from './check-in-delete-dialog';

const Routes = ({ match }) => (
  <>
    <Switch>
      <ErrorBoundaryRoute exact path={`${match.url}/new`} component={CheckInUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id/edit`} component={CheckInUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id`} component={CheckInDetail} />
      <ErrorBoundaryRoute path={match.url} component={CheckIn} />
    </Switch>
    <ErrorBoundaryRoute exact path={`${match.url}/:id/delete`} component={CheckInDeleteDialog} />
  </>
);

export default Routes;
