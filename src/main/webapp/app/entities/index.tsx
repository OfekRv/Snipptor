import React from 'react';
import { Switch } from 'react-router-dom';

// eslint-disable-next-line @typescript-eslint/no-unused-vars
import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import Snippet from './snippet';
import Rule from './rule';
import Vulnerability from './vulnerability';
import Engine from './engine';
import SnippetMatchedRules from './snippet-matched-rules';
/* jhipster-needle-add-route-import - JHipster will add routes here */

const Routes = ({ match }) => (
  <div>
    <Switch>
      {/* prettier-ignore */}
      <ErrorBoundaryRoute path={`${match.url}snippet`} component={Snippet} />
      <ErrorBoundaryRoute path={`${match.url}rule`} component={Rule} />
      <ErrorBoundaryRoute path={`${match.url}vulnerability`} component={Vulnerability} />
      <ErrorBoundaryRoute path={`${match.url}engine`} component={Engine} />
      <ErrorBoundaryRoute path={`${match.url}snippet-matched-rules`} component={SnippetMatchedRules} />
      {/* jhipster-needle-add-route-path - JHipster will add routes here */}
    </Switch>
  </div>
);

export default Routes;
