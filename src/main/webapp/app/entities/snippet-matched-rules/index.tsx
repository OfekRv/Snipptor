import React from 'react';
import { Switch } from 'react-router-dom';

import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import SnippetMatchedRules from './snippet-matched-rules';
import SnippetMatchedRulesDetail from './snippet-matched-rules-detail';
import SnippetMatchedRulesUpdate from './snippet-matched-rules-update';
import SnippetMatchedRulesDeleteDialog from './snippet-matched-rules-delete-dialog';

const Routes = ({ match }) => (
  <>
    <Switch>
      <ErrorBoundaryRoute exact path={`${match.url}/new`} component={SnippetMatchedRulesUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id/edit`} component={SnippetMatchedRulesUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id`} component={SnippetMatchedRulesDetail} />
      <ErrorBoundaryRoute path={match.url} component={SnippetMatchedRules} />
    </Switch>
    <ErrorBoundaryRoute exact path={`${match.url}/:id/delete`} component={SnippetMatchedRulesDeleteDialog} />
  </>
);

export default Routes;
