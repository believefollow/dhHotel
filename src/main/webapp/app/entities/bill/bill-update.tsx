import React, { useState, useEffect } from 'react';
import { connect } from 'react-redux';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col, Label } from 'reactstrap';
import { AvFeedback, AvForm, AvGroup, AvInput, AvField } from 'availity-reactstrap-validation';
import { ICrudGetAction, ICrudGetAllAction, ICrudPutAction } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { IRootState } from 'app/shared/reducers';

import { ICheckIn } from 'app/shared/model/check-in.model';
import { getEntities as getCheckIns } from 'app/entities/check-in/check-in.reducer';
import { getEntity, updateEntity, createEntity, reset } from './bill.reducer';
import { IBill } from 'app/shared/model/bill.model';
import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';

export interface IBillUpdateProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export const BillUpdate = (props: IBillUpdateProps) => {
  const [checkInId, setCheckInId] = useState('0');
  const [isNew, setIsNew] = useState(!props.match.params || !props.match.params.id);

  const { billEntity, checkIns, loading, updating } = props;

  const handleClose = () => {
    props.history.push('/bill');
  };

  useEffect(() => {
    if (!isNew) {
      props.getEntity(props.match.params.id);
    }

    props.getCheckIns();
  }, []);

  useEffect(() => {
    if (props.updateSuccess) {
      handleClose();
    }
  }, [props.updateSuccess]);

  const saveEntity = (event, errors, values) => {
    if (errors.length === 0) {
      const entity = {
        ...billEntity,
        ...values,
      };

      if (isNew) {
        props.createEntity(entity);
      } else {
        props.updateEntity(entity);
      }
    }
  };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="dhHotelApp.bill.home.createOrEditLabel">Create or edit a Bill</h2>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Loading...</p>
          ) : (
            <AvForm model={isNew ? {} : billEntity} onSubmit={saveEntity}>
              {!isNew ? (
                <AvGroup>
                  <Label for="bill-id">ID</Label>
                  <AvInput id="bill-id" type="text" className="form-control" name="id" required readOnly />
                </AvGroup>
              ) : null}
              <AvGroup>
                <Label id="balanceLabel" for="bill-balance">
                  Balance
                </Label>
                <AvField id="bill-balance" type="text" name="balance" />
              </AvGroup>
              <AvGroup>
                <Label id="sourceLabel" for="bill-source">
                  Source
                </Label>
                <AvInput
                  id="bill-source"
                  type="select"
                  className="form-control"
                  name="source"
                  value={(!isNew && billEntity.source) || 'AliPay'}
                >
                  <option value="AliPay">AliPay</option>
                  <option value="WeChat">WeChat</option>
                </AvInput>
              </AvGroup>
              <AvGroup>
                <Label for="bill-checkIn">Check In</Label>
                <AvInput id="bill-checkIn" type="select" className="form-control" name="checkIn.id">
                  <option value="" key="0" />
                  {checkIns
                    ? checkIns.map(otherEntity => (
                        <option value={otherEntity.id} key={otherEntity.id}>
                          {otherEntity.id}
                        </option>
                      ))
                    : null}
                </AvInput>
              </AvGroup>
              <Button tag={Link} id="cancel-save" to="/bill" replace color="info">
                <FontAwesomeIcon icon="arrow-left" />
                &nbsp;
                <span className="d-none d-md-inline">Back</span>
              </Button>
              &nbsp;
              <Button color="primary" id="save-entity" type="submit" disabled={updating}>
                <FontAwesomeIcon icon="save" />
                &nbsp; Save
              </Button>
            </AvForm>
          )}
        </Col>
      </Row>
    </div>
  );
};

const mapStateToProps = (storeState: IRootState) => ({
  checkIns: storeState.checkIn.entities,
  billEntity: storeState.bill.entity,
  loading: storeState.bill.loading,
  updating: storeState.bill.updating,
  updateSuccess: storeState.bill.updateSuccess,
});

const mapDispatchToProps = {
  getCheckIns,
  getEntity,
  updateEntity,
  createEntity,
  reset,
};

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(mapStateToProps, mapDispatchToProps)(BillUpdate);
