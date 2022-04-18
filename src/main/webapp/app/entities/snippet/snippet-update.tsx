import React, { useState, useEffect } from 'react';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col, FormText } from 'reactstrap';
import { isNumber, Translate, translate, ValidatedField, ValidatedForm } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { ISnippetMatchedRules } from 'app/shared/model/snippet-matched-rules.model';
import { getEntities as getSnippetMatchedRules } from 'app/entities/snippet-matched-rules/snippet-matched-rules.reducer';
import { getEntity, updateEntity, createEntity, reset } from './snippet.reducer';
import { ISnippet } from 'app/shared/model/snippet.model';
import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';
import { SnippetClassification } from 'app/shared/model/enumerations/snippet-classification.model';

export const SnippetUpdate = (props: RouteComponentProps<{ id: string }>) => {
  const dispatch = useAppDispatch();

  const [isNew] = useState(!props.match.params || !props.match.params.id);

  const snippetMatchedRules = useAppSelector(state => state.snippetMatchedRules.entities);
  const snippetEntity = useAppSelector(state => state.snippet.entity);
  const loading = useAppSelector(state => state.snippet.loading);
  const updating = useAppSelector(state => state.snippet.updating);
  const updateSuccess = useAppSelector(state => state.snippet.updateSuccess);
  const snippetClassificationValues = Object.keys(SnippetClassification);
  const handleClose = () => {
    props.history.push('/snippet' + props.location.search);
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(props.match.params.id));
    }

    dispatch(getSnippetMatchedRules({}));
  }, []);

  useEffect(() => {
    if (updateSuccess) {
      handleClose();
    }
  }, [updateSuccess]);

  const saveEntity = values => {
    const entity = {
      ...snippetEntity,
      ...values,
    };

    if (isNew) {
      dispatch(createEntity(entity));
    } else {
      dispatch(updateEntity(entity));
    }
  };

  const defaultValues = () =>
    isNew
      ? {}
      : {
          classification: 'UNKNOWN',
          ...snippetEntity
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="snipptorApp.snippet.home.createOrEditLabel" data-cy="SnippetCreateUpdateHeading">
            <Translate contentKey="snipptorApp.snippet.home.createOrEditLabel">Create or edit a Snippet</Translate>
          </h2>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Loading...</p>
          ) : (
            <ValidatedForm defaultValues={defaultValues()} onSubmit={saveEntity}>
              {!isNew ? (
                <ValidatedField
                  name="id"
                  required
                  readOnly
                  id="snippet-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField label={translate('snipptorApp.snippet.hash')} id="snippet-hash" name="hash" data-cy="hash" type="text" disabled/>
              <ValidatedField
                label={translate('snipptorApp.snippet.content')}
                id="snippet-content"
                name="content"
                data-cy="content"
                type="textarea"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
                disabled={!isNew}
               />
              <ValidatedField label={translate('snipptorApp.snippet.url')} id="snippet-url" name="url" data-cy="url" type="text" disabled={!isNew}/>
              <ValidatedField
                label={translate('snipptorApp.snippet.classification')}
                id="snippet-classification"
                name="classification"
                data-cy="classification"
                type="select"
              >
                {snippetClassificationValues.map(snippetClassification => (
                  <option value={snippetClassification} key={snippetClassification}>
                    {translate('snipptorApp.SnippetClassification.' + snippetClassification)}
                  </option>
                ))}
              </ValidatedField>
              <ValidatedField
                label={translate('snipptorApp.snippet.scanCount')}
                id="snippet-scanCount"
                name="scanCount"
                data-cy="scanCount"
                type="text"
                disabled
              />
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/snippet" replace color="info">
                <FontAwesomeIcon icon="arrow-left" />
                &nbsp;
                <span className="d-none d-md-inline">
                  <Translate contentKey="entity.action.back">Back</Translate>
                </span>
              </Button>
              &nbsp;
              <Button color="primary" id="save-entity" data-cy="entityCreateSaveButton" type="submit" disabled={updating}>
                <FontAwesomeIcon icon="save" />
                &nbsp;
                <Translate contentKey="entity.action.save">Save</Translate>
              </Button>
            </ValidatedForm>
          )}
        </Col>
      </Row>
    </div>
  );
};

export default SnippetUpdate;
