import React, { useState, useEffect } from 'react';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col, FormText } from 'reactstrap';
import { isNumber, Translate, translate, ValidatedField, ValidatedForm } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { IEngine } from 'app/shared/model/engine.model';
import { getEntities as getEngines } from 'app/entities/engine/engine.reducer';
import { IVulnerability } from 'app/shared/model/vulnerability.model';
import { getEntities as getVulnerabilities } from 'app/entities/vulnerability/vulnerability.reducer';
import { ISnippetMatchedRules } from 'app/shared/model/snippet-matched-rules.model';
import { getEntities as getSnippetMatchedRules } from 'app/entities/snippet-matched-rules/snippet-matched-rules.reducer';
import { getEntity, updateEntity, createEntity, reset } from './rule.reducer';
import { IRule } from 'app/shared/model/rule.model';
import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

export const RuleUpdate = (props: RouteComponentProps<{ id: string }>) => {
  const dispatch = useAppDispatch();

  const [isNew] = useState(!props.match.params || !props.match.params.id);

  const engines = useAppSelector(state => state.engine.entities);
  const vulnerabilities = useAppSelector(state => state.vulnerability.entities);
  const snippetMatchedRules = useAppSelector(state => state.snippetMatchedRules.entities);
  const ruleEntity = useAppSelector(state => state.rule.entity);
  const loading = useAppSelector(state => state.rule.loading);
  const updating = useAppSelector(state => state.rule.updating);
  const updateSuccess = useAppSelector(state => state.rule.updateSuccess);
  const handleClose = () => {
    props.history.push('/rule' + props.location.search);
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(props.match.params.id));
    }

    dispatch(getEngines({}));
    dispatch(getVulnerabilities({}));
    dispatch(getSnippetMatchedRules({}));
  }, []);

  useEffect(() => {
    if (updateSuccess) {
      handleClose();
    }
  }, [updateSuccess]);

  const saveEntity = values => {
    const entity = {
      ...ruleEntity,
      ...values,
      snippetMatchedRules: mapIdList(values.snippetMatchedRules),
      engine: engines.find(it => it.id.toString() === values.engine.toString()),
      vulnerability: vulnerabilities.find(it => it.id.toString() === values.vulnerability.toString()),
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
          ...ruleEntity,
          engine: ruleEntity?.engine?.id,
          vulnerability: ruleEntity?.vulnerability?.id,
          snippetMatchedRules: ruleEntity?.snippetMatchedRules?.map(e => e.id.toString()),
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="snipptorApp.rule.home.createOrEditLabel" data-cy="RuleCreateUpdateHeading">
            <Translate contentKey="snipptorApp.rule.home.createOrEditLabel">Create or edit a Rule</Translate>
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
                  id="rule-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label={translate('snipptorApp.rule.name')}
                id="rule-name"
                name="name"
                data-cy="name"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField label={translate('snipptorApp.rule.raw')} id="rule-raw" name="raw" data-cy="raw" type="textarea" rows={10}/>
              <ValidatedField id="rule-engine" name="engine" data-cy="engine" label={translate('snipptorApp.rule.engine')} type="select">
                <option value="" key="0" />
                {engines
                  ? engines.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.name}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <ValidatedField
                id="rule-vulnerability"
                name="vulnerability"
                data-cy="vulnerability"
                label={translate('snipptorApp.rule.vulnerability')}
                type="select"
              >
                <option value="" key="0" />
                {vulnerabilities
                  ? vulnerabilities.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <ValidatedField
                label={translate('snipptorApp.rule.snippetMatchedRules')}
                id="rule-snippetMatchedRules"
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
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/rule" replace color="info">
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

export default RuleUpdate;
