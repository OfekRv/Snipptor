import React, { useEffect } from 'react';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { Translate, byteSize } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { getEntity } from './rule.reducer';
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

export const RuleDetail = (props: RouteComponentProps<{ id: string }>) => {
  const dispatch = useAppDispatch();

  useEffect(() => {
    dispatch(getEntity(props.match.params.id));
  }, []);

  const ruleEntity = useAppSelector(state => state.rule.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="ruleDetailsHeading">
          <Translate contentKey="snipptorApp.rule.detail.title">Rule</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{ruleEntity.id}</dd>
          <dt>
            <span id="name">
              <Translate contentKey="snipptorApp.rule.name">Name</Translate>
            </span>
          </dt>
          <dd>{ruleEntity.name}</dd>
          <dt>
            <span id="raw">
              <Translate contentKey="snipptorApp.rule.raw">Raw</Translate>
            </span>
          </dt>
          <dd>{ruleEntity.raw}</dd>
          <dt>
            <Translate contentKey="snipptorApp.rule.engine">Engine</Translate>
          </dt>
          <dd>{ruleEntity.engine ? ruleEntity.engine.id : ''}</dd>
          <dt>
            <Translate contentKey="snipptorApp.rule.vulnerability">Vulnerability</Translate>
          </dt>
          <dd>{ruleEntity.vulnerability ? ruleEntity.vulnerability.id : ''}</dd>
          <dt>
            <Translate contentKey="snipptorApp.rule.snippetMatchedRules">Snippet Matched Rules</Translate>
          </dt>
          <dd>
            {ruleEntity.snippetMatchedRules
              ? ruleEntity.snippetMatchedRules.map((val, i) => (
                  <span key={val.id}>
                    <a>{val.id}</a>
                    {ruleEntity.snippetMatchedRules && i === ruleEntity.snippetMatchedRules.length - 1 ? '' : ', '}
                  </span>
                ))
              : null}
          </dd>
        </dl>
        <Button tag={Link} to="/rule" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/rule/${ruleEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default RuleDetail;
