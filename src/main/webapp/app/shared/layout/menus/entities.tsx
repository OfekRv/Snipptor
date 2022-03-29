import React from 'react';
import MenuItem from 'app/shared/layout/menus/menu-item';
import { Translate, translate } from 'react-jhipster';
import { NavDropdown } from './menu-components';

export const EntitiesMenu = props => (
  <NavDropdown
    icon="th-list"
    name={translate('global.menu.entities.main')}
    id="entity-menu"
    data-cy="entity"
    style={{ maxHeight: '80vh', overflow: 'auto' }}
  >
    <>{/* to avoid warnings when empty */}</>
    <MenuItem icon="asterisk" to="/snippet">
      <Translate contentKey="global.menu.entities.snippet" />
    </MenuItem>
    <MenuItem icon="asterisk" to="/rule">
      <Translate contentKey="global.menu.entities.rule" />
    </MenuItem>
    <MenuItem icon="asterisk" to="/vulnerability">
      <Translate contentKey="global.menu.entities.vulnerability" />
    </MenuItem>
    <MenuItem icon="asterisk" to="/engine">
      <Translate contentKey="global.menu.entities.engine" />
    </MenuItem>
    <MenuItem icon="asterisk" to="/snippet-matched-rules">
      <Translate contentKey="global.menu.entities.snippetMatchedRules" />
    </MenuItem>
    {/* jhipster-needle-add-entity-to-menu - JHipster will add entities to the menu here */}
  </NavDropdown>
);
