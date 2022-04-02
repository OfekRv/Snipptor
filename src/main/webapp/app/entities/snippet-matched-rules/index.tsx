import React from 'react';
import { Switch } from 'react-router-dom';

import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import SnippetMatchedRules from './snippet-matched-rules';
import SnippetMatchedRulesDetail from './snippet-matched-rules-detail';

const Routes = ({ match }) => (
  <>
    <Switch>
      <ErrorBoundaryRoute exact path={`${match.url}/:id`} component={SnippetMatchedRulesDetail} />
      <ErrorBoundaryRoute path={match.url} component={SnippetMatchedRules} />
    </Switch>
  </>
);

export default Routes;
