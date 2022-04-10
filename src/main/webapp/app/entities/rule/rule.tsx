import React, { useState, useEffect } from 'react';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Table } from 'reactstrap';
import { Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { getEntities } from './rule.reducer';
import { IRule } from 'app/shared/model/rule.model';
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

export const Rule = (props: RouteComponentProps<{ url: string }>) => {
  const dispatch = useAppDispatch();

  const ruleList = useAppSelector(state => state.rule.entities);
  const loading = useAppSelector(state => state.rule.loading);

  useEffect(() => {
    dispatch(getEntities({}));
  }, []);

  const handleSyncList = () => {
    dispatch(getEntities({}));
  };

  const { match } = props;

  return (
    <div>
      <h2 id="rule-heading" data-cy="RuleHeading">
        <Translate contentKey="snipptorApp.rule.home.title">Rules</Translate>
        <div className="d-flex justify-content-end">
          <Button className="me-2" color="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} />{' '}
            <Translate contentKey="snipptorApp.rule.home.refreshListLabel">Refresh List</Translate>
          </Button>
          <Link to={`${match.url}/new`} className="btn btn-primary jh-create-entity" id="jh-create-entity" data-cy="entityCreateButton">
            <FontAwesomeIcon icon="plus" />
            &nbsp;
            <Translate contentKey="snipptorApp.rule.home.createLabel">Create new Rule</Translate>
          </Link>
        </div>
      </h2>
      <div className="table-responsive">
        {ruleList && ruleList.length > 0 ? (
          <Table responsive>
            <thead>
              <tr>
                <th>
                  <Translate contentKey="snipptorApp.rule.id">ID</Translate>
                </th>
                <th>
                  <Translate contentKey="snipptorApp.rule.name">Name</Translate>
                </th>
                <th>
                  <Translate contentKey="snipptorApp.rule.engine">Engine</Translate>
                </th>
                <th>
                  <Translate contentKey="snipptorApp.rule.vulnerability">Vulnerability</Translate>
                </th>
                <th>
                  <Translate contentKey="snipptorApp.rule.snippetMatchedRules">Snippet Matched Rules</Translate>
                </th>
                <th />
              </tr>
            </thead>
            <tbody>
              {ruleList.map((rule, i) => (
                <tr key={`entity-${i}`} data-cy="entityTable">
                  <td>
                    <Button tag={Link} to={`${match.url}/${rule.id}`} color="link" size="sm">
                      {rule.id}
                    </Button>
                  </td>
                  <td>{rule.name}</td>
                  <td>{rule.engine ? <Link to={`engine/${rule.engine.id}`}>{rule.engine.id}</Link> : ''}</td>
                  <td>{rule.vulnerability ? <Link to={`vulnerability/${rule.vulnerability.id}`}>{rule.vulnerability.id}</Link> : ''}</td>
                  <td>
                    {rule.snippetMatchedRules
                      ? rule.snippetMatchedRules.map((val, j) => (
                          <span key={j}>
                            <Link to={`snippet-matched-rules/${val.id}`}>{val.id}</Link>
                            {j === rule.snippetMatchedRules.length - 1 ? '' : ', '}
                          </span>
                        ))
                      : null}
                  </td>
                  <td className="text-end">
                    <div className="btn-group flex-btn-group-container">
                      <Button tag={Link} to={`${match.url}/${rule.id}`} color="info" size="sm" data-cy="entityDetailsButton">
                        <FontAwesomeIcon icon="eye" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.view">View</Translate>
                        </span>
                      </Button>
                      <Button tag={Link} to={`${match.url}/${rule.id}/edit`} color="primary" size="sm" data-cy="entityEditButton">
                        <FontAwesomeIcon icon="pencil-alt" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.edit">Edit</Translate>
                        </span>
                      </Button>
                      <Button tag={Link} to={`${match.url}/${rule.id}/delete`} color="danger" size="sm" data-cy="entityDeleteButton">
                        <FontAwesomeIcon icon="trash" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.delete">Delete</Translate>
                        </span>
                      </Button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </Table>
        ) : (
          !loading && (
            <div className="alert alert-warning">
              <Translate contentKey="snipptorApp.rule.home.notFound">No Rules found</Translate>
            </div>
          )
        )}
      </div>
    </div>
  );
};

export default Rule;
