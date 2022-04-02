import React, { useEffect } from 'react';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { Translate, byteSize } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { getEntity } from './snippet.reducer';
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

export const SnippetDetail = (props: RouteComponentProps<{ id: string }>) => {
  const dispatch = useAppDispatch();

  useEffect(() => {
    dispatch(getEntity(props.match.params.id));
  }, []);

  const snippetEntity = useAppSelector(state => state.snippet.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="snippetDetailsHeading">
          <Translate contentKey="snipptorApp.snippet.detail.title">Snippet</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{snippetEntity.id}</dd>
          <dt>
            <span id="hash">
              <Translate contentKey="snipptorApp.snippet.hash">Hash</Translate>
            </span>
          </dt>
          <dd>{snippetEntity.hash}</dd>
          <dt>
            <span id="content">
              <Translate contentKey="snipptorApp.snippet.content">Content</Translate>
            </span>
          </dt>
          <dd>{snippetEntity.content}</dd>
          <dt>
            <span id="url">
              <Translate contentKey="snipptorApp.snippet.url">Url</Translate>
            </span>
          </dt>
          <dd>{snippetEntity.url}</dd>
          <dt>
            <span id="classification">
              <Translate contentKey="snipptorApp.snippet.classification">Classification</Translate>
            </span>
          </dt>
          <dd>{snippetEntity.classification}</dd>
          <dt>
            <span id="scanCount">
              <Translate contentKey="snipptorApp.snippet.scanCount">Scan Count</Translate>
            </span>
          </dt>
          <dd>{snippetEntity.scanCount}</dd>
          <dt>
            <Translate contentKey="snipptorApp.snippet.snippetMatchedRules">Snippet Matched Rules</Translate>
          </dt>
          <dd>
            {snippetEntity.snippetMatchedRules
              ? snippetEntity.snippetMatchedRules.map((val, i) => (
                  <span key={val.id}>
                    <a>{val.id}</a>
                    {snippetEntity.snippetMatchedRules && i === snippetEntity.snippetMatchedRules.length - 1 ? '' : ', '}
                  </span>
                ))
              : null}
          </dd>
        </dl>
        <Button tag={Link} to="/snippet" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/snippet/${snippetEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default SnippetDetail;
