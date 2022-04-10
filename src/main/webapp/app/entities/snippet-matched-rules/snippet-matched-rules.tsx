import React, { useState, useEffect } from 'react';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Table } from 'reactstrap';
import { Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { getEntities } from './snippet-matched-rules.reducer';
import { ISnippetMatchedRules } from 'app/shared/model/snippet-matched-rules.model';
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

export const SnippetMatchedRules = (props: RouteComponentProps<{ url: string }>) => {
  const dispatch = useAppDispatch();

  const snippetMatchedRulesList = useAppSelector(state => state.snippetMatchedRules.entities);
  const loading = useAppSelector(state => state.snippetMatchedRules.loading);

  useEffect(() => {
    dispatch(getEntities({}));
  }, []);

  const handleSyncList = () => {
    dispatch(getEntities({}));
  };

  const { match } = props;

  return (
    <div>
      <h2 id="snippet-matched-rules-heading" data-cy="SnippetMatchedRulesHeading">
        <Translate contentKey="snipptorApp.snippetMatchedRules.home.title">Snippet Matched Rules</Translate>
        <div className="d-flex justify-content-end">
          <Button className="me-2" color="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} />{' '}
            <Translate contentKey="snipptorApp.snippetMatchedRules.home.refreshListLabel">Refresh List</Translate>
          </Button>
          <Link to={`${match.url}/new`} className="btn btn-primary jh-create-entity" id="jh-create-entity" data-cy="entityCreateButton">
            <FontAwesomeIcon icon="plus" />
            &nbsp;
            <Translate contentKey="snipptorApp.snippetMatchedRules.home.createLabel">Create new Snippet Matched Rules</Translate>
          </Link>
        </div>
      </h2>
      <div className="table-responsive">
        {snippetMatchedRulesList && snippetMatchedRulesList.length > 0 ? (
          <Table responsive>
            <thead>
              <tr>
                <th>
                  <Translate contentKey="snipptorApp.snippetMatchedRules.id">ID</Translate>
                </th>
                <th />
              </tr>
            </thead>
            <tbody>
              {snippetMatchedRulesList.map((snippetMatchedRules, i) => (
                <tr key={`entity-${i}`} data-cy="entityTable">
                  <td>
                    <Button tag={Link} to={`${match.url}/${snippetMatchedRules.id}`} color="link" size="sm">
                      {snippetMatchedRules.id}
                    </Button>
                  </td>
                  <td className="text-end">
                    <div className="btn-group flex-btn-group-container">
                      <Button tag={Link} to={`${match.url}/${snippetMatchedRules.id}`} color="info" size="sm" data-cy="entityDetailsButton">
                        <FontAwesomeIcon icon="eye" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.view">View</Translate>
                        </span>
                      </Button>
                      <Button
                        tag={Link}
                        to={`${match.url}/${snippetMatchedRules.id}/edit`}
                        color="primary"
                        size="sm"
                        data-cy="entityEditButton"
                      >
                        <FontAwesomeIcon icon="pencil-alt" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.edit">Edit</Translate>
                        </span>
                      </Button>
                      <Button
                        tag={Link}
                        to={`${match.url}/${snippetMatchedRules.id}/delete`}
                        color="danger"
                        size="sm"
                        data-cy="entityDeleteButton"
                      >
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
              <Translate contentKey="snipptorApp.snippetMatchedRules.home.notFound">No Snippet Matched Rules found</Translate>
            </div>
          )
        )}
      </div>
    </div>
  );
};

export default SnippetMatchedRules;
