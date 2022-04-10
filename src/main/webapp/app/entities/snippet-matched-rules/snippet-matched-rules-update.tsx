import React, { useState, useEffect } from 'react';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col, FormText } from 'reactstrap';
import { isNumber, Translate, translate, ValidatedField, ValidatedForm } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { IRule } from 'app/shared/model/rule.model';
import { getEntities as getRules } from 'app/entities/rule/rule.reducer';
import { ISnippet } from 'app/shared/model/snippet.model';
import { getEntities as getSnippets } from 'app/entities/snippet/snippet.reducer';
import { getEntity, reset } from './snippet-matched-rules.reducer';
import { ISnippetMatchedRules } from 'app/shared/model/snippet-matched-rules.model';
import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

export const SnippetMatchedRulesUpdate = (props: RouteComponentProps<{ id: string }>) => {
  const dispatch = useAppDispatch();

  const [isNew] = useState(!props.match.params || !props.match.params.id);

  const rules = useAppSelector(state => state.rule.entities);
  const snippets = useAppSelector(state => state.snippet.entities);
  const snippetMatchedRulesEntity = useAppSelector(state => state.snippetMatchedRules.entity);
  const loading = useAppSelector(state => state.snippetMatchedRules.loading);
  const updating = useAppSelector(state => state.snippetMatchedRules.updating);
  const updateSuccess = useAppSelector(state => state.snippetMatchedRules.updateSuccess);
  const handleClose = () => {
    props.history.push('/snippet-matched-rules');
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(props.match.params.id));
    }

    dispatch(getRules({}));
    dispatch(getSnippets({}));
  }, []);

  useEffect(() => {
    if (updateSuccess) {
      handleClose();
    }
  }, [updateSuccess]);

  const saveEntity = values => {
    const entity = {
      ...snippetMatchedRulesEntity,
      ...values,
    };
  };

  const defaultValues = () =>
    isNew
      ? {}
      : {
          ...snippetMatchedRulesEntity,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="snipptorApp.snippetMatchedRules.home.createOrEditLabel" data-cy="SnippetMatchedRulesCreateUpdateHeading">
            <Translate contentKey="snipptorApp.snippetMatchedRules.home.createOrEditLabel">Create or edit a SnippetMatchedRules</Translate>
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
                  id="snippet-matched-rules-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/snippet-matched-rules" replace color="info">
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

export default SnippetMatchedRulesUpdate;
