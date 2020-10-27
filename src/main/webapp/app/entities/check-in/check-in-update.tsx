import React, { useState, useEffect } from 'react';
import { connect } from 'react-redux';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col, Label } from 'reactstrap';
import { AvFeedback, AvForm, AvGroup, AvInput, AvField } from 'availity-reactstrap-validation';
import { ICrudGetAction, ICrudGetAllAction, ICrudPutAction } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { IRootState } from 'app/shared/reducers';

import { getEntity, updateEntity, createEntity, reset } from './check-in.reducer';
import { ICheckIn } from 'app/shared/model/check-in.model';
import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';

export interface ICheckInUpdateProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export const CheckInUpdate = (props: ICheckInUpdateProps) => {
  const [isNew, setIsNew] = useState(!props.match.params || !props.match.params.id);

  const { checkInEntity, loading, updating } = props;

  const handleClose = () => {
    props.history.push('/check-in');
  };

  useEffect(() => {
    if (!isNew) {
      props.getEntity(props.match.params.id);
    }
  }, []);

  useEffect(() => {
    if (props.updateSuccess) {
      handleClose();
    }
  }, [props.updateSuccess]);

  const saveEntity = (event, errors, values) => {
    if (errors.length === 0) {
      const entity = {
        ...checkInEntity,
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
          <h2 id="dhHotelApp.checkIn.home.createOrEditLabel">Create or edit a CheckIn</h2>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Loading...</p>
          ) : (
            <AvForm model={isNew ? {} : checkInEntity} onSubmit={saveEntity}>
              {!isNew ? (
                <AvGroup>
                  <Label for="check-in-id">ID</Label>
                  <AvInput id="check-in-id" type="text" className="form-control" name="id" required readOnly />
                </AvGroup>
              ) : null}
              <AvGroup>
                <Label id="startTimeLabel" for="check-in-startTime">
                  Start Time
                </Label>
                <AvField id="check-in-startTime" type="date" className="form-control" name="startTime" />
              </AvGroup>
              <AvGroup>
                <Label id="endTimeLabel" for="check-in-endTime">
                  End Time
                </Label>
                <AvField id="check-in-endTime" type="date" className="form-control" name="endTime" />
              </AvGroup>
              <Button tag={Link} id="cancel-save" to="/check-in" replace color="info">
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
  checkInEntity: storeState.checkIn.entity,
  loading: storeState.checkIn.loading,
  updating: storeState.checkIn.updating,
  updateSuccess: storeState.checkIn.updateSuccess,
});

const mapDispatchToProps = {
  getEntity,
  updateEntity,
  createEntity,
  reset,
};

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(mapStateToProps, mapDispatchToProps)(CheckInUpdate);
