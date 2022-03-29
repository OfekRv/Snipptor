import React from 'react';
import { Switch } from 'react-router-dom';

import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import Engine from './engine';
import EngineDetail from './engine-detail';
import EngineUpdate from './engine-update';
import EngineDeleteDialog from './engine-delete-dialog';

const Routes = ({ match }) => (
  <>
    <Switch>
      <ErrorBoundaryRoute exact path={`${match.url}/new`} component={EngineUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id/edit`} component={EngineUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id`} component={EngineDetail} />
      <ErrorBoundaryRoute path={match.url} component={Engine} />
    </Switch>
    <ErrorBoundaryRoute exact path={`${match.url}/:id/delete`} component={EngineDeleteDialog} />
  </>
);

export default Routes;
