import React, { useState, useEffect } from 'react';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Table } from 'reactstrap';
import { Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { getEntities } from './snippet.reducer';
import { ISnippet } from 'app/shared/model/snippet.model';
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

export const Snippet = (props: RouteComponentProps<{ url: string }>) => {
  const dispatch = useAppDispatch();

  const snippetList = useAppSelector(state => state.snippet.entities);
  const loading = useAppSelector(state => state.snippet.loading);

  useEffect(() => {
    dispatch(getEntities({}));
  }, []);

  const handleSyncList = () => {
    dispatch(getEntities({}));
  };

  const { match } = props;

  return (
    <div>
      <h2 id="snippet-heading" data-cy="SnippetHeading">
        <Translate contentKey="snipptorApp.snippet.home.title">Snippets</Translate>
        <div className="d-flex justify-content-end">
          <Button className="me-2" color="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} />{' '}
            <Translate contentKey="snipptorApp.snippet.home.refreshListLabel">Refresh List</Translate>
          </Button>
          <Link to={`${match.url}/new`} className="btn btn-primary jh-create-entity" id="jh-create-entity" data-cy="entityCreateButton">
            <FontAwesomeIcon icon="plus" />
            &nbsp;
            <Translate contentKey="snipptorApp.snippet.home.createLabel">Create new Snippet</Translate>
          </Link>
        </div>
      </h2>
      <div className="table-responsive">
        {snippetList && snippetList.length > 0 ? (
          <Table responsive>
            <thead>
              <tr>
                <th>
                  <Translate contentKey="snipptorApp.snippet.id">ID</Translate>
                </th>
                <th>
                  <Translate contentKey="snipptorApp.snippet.hash">Hash</Translate>
                </th>
                <th>
                  <Translate contentKey="snipptorApp.snippet.content">Content</Translate>
                </th>
                <th>
                  <Translate contentKey="snipptorApp.snippet.url">Url</Translate>
                </th>
                <th>
                  <Translate contentKey="snipptorApp.snippet.snippetMatchedRules">Snippet Matched Rules</Translate>
                </th>
                <th />
              </tr>
            </thead>
            <tbody>
              {snippetList.map((snippet, i) => (
                <tr key={`entity-${i}`} data-cy="entityTable">
                  <td>
                    <Button tag={Link} to={`${match.url}/${snippet.id}`} color="link" size="sm">
                      {snippet.id}
                    </Button>
                  </td>
                  <td>{snippet.hash}</td>
                  <td>{snippet.content}</td>
                  <td>{snippet.url}</td>
                  <td>
                    {snippet.snippetMatchedRules
                      ? snippet.snippetMatchedRules.map((val, j) => (
                          <span key={j}>
                            <Link to={`snippet-matched-rules/${val.id}`}>{val.id}</Link>
                            {j === snippet.snippetMatchedRules.length - 1 ? '' : ', '}
                          </span>
                        ))
                      : null}
                  </td>
                  <td className="text-end">
                    <div className="btn-group flex-btn-group-container">
                      <Button tag={Link} to={`${match.url}/${snippet.id}`} color="info" size="sm" data-cy="entityDetailsButton">
                        <FontAwesomeIcon icon="eye" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.view">View</Translate>
                        </span>
                      </Button>
                      <Button tag={Link} to={`${match.url}/${snippet.id}/edit`} color="primary" size="sm" data-cy="entityEditButton">
                        <FontAwesomeIcon icon="pencil-alt" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.edit">Edit</Translate>
                        </span>
                      </Button>
                      <Button tag={Link} to={`${match.url}/${snippet.id}/delete`} color="danger" size="sm" data-cy="entityDeleteButton">
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
              <Translate contentKey="snipptorApp.snippet.home.notFound">No Snippets found</Translate>
            </div>
          )
        )}
      </div>
    </div>
  );
};

export default Snippet;
