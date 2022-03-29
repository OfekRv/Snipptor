import React from 'react';
import { Switch } from 'react-router-dom';

import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import Snippet from './snippet';
import SnippetDetail from './snippet-detail';
import SnippetUpdate from './snippet-update';
import SnippetDeleteDialog from './snippet-delete-dialog';

const Routes = ({ match }) => (
  <>
    <Switch>
      <ErrorBoundaryRoute exact path={`${match.url}/new`} component={SnippetUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id`} component={SnippetDetail} />
      <ErrorBoundaryRoute path={match.url} component={Snippet} />
    </Switch>
    <ErrorBoundaryRoute exact path={`${match.url}/:id/delete`} component={SnippetDeleteDialog} />
  </>
);

export default Routes;
