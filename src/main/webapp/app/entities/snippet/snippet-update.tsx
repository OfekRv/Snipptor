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

export const SnippetUpdate = (props: RouteComponentProps<{ id: string }>) => {
  const dispatch = useAppDispatch();

  const [isNew] = useState(!props.match.params || !props.match.params.id);

  const snippetMatchedRules = useAppSelector(state => state.snippetMatchedRules.entities);
  const snippetEntity = useAppSelector(state => state.snippet.entity);
  const loading = useAppSelector(state => state.snippet.loading);
  const updating = useAppSelector(state => state.snippet.updating);
  const updateSuccess = useAppSelector(state => state.snippet.updateSuccess);
  const handleClose = () => {
    props.history.push('/snippet');
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
      snippetMatchedRules: mapIdList(values.snippetMatchedRules),
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
          ...snippetEntity,
          snippetMatchedRules: snippetEntity?.snippetMatchedRules?.map(e => e.id.toString()),
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
              <ValidatedField label={translate('snipptorApp.snippet.hash')} id="snippet-hash" name="hash" data-cy="hash" type="text" />
              <ValidatedField
                label={translate('snipptorApp.snippet.content')}
                id="snippet-content"
                name="content"
                data-cy="content"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField label={translate('snipptorApp.snippet.url')} id="snippet-url" name="url" data-cy="url" type="text" />
              <ValidatedField
                label={translate('snipptorApp.snippet.snippetMatchedRules')}
                id="snippet-snippetMatchedRules"
                data-cy="snippetMatchedRules"
                type="select"
                multiple
                name="snippetMatchedRules"
              >
                <option value="" key="0" />
                {snippetMatchedRules
                  ? snippetMatchedRules.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
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
