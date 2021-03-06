import React from 'react';
import { Switch } from 'react-router-dom';

import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import Snippet from './snippet';
import SnippetDetail from './snippet-detail';
import SnippetUpdate from './snippet-update';

const Routes = ({ match }) => (
  <>
    <Switch>
      <ErrorBoundaryRoute exact path={`${match.url}/new`} component={SnippetUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id/edit`} component={SnippetUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id`} component={SnippetDetail} />
      <ErrorBoundaryRoute path={match.url} component={Snippet} />
    </Switch>
  </>
);

export default Routes;
