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
import { getEntity, updateEntity, createEntity, reset } from './room.reducer';
import { IRoom } from 'app/shared/model/room.model';
import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';

export interface IRoomUpdateProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export const RoomUpdate = (props: IRoomUpdateProps) => {
  const [checkInId, setCheckInId] = useState('0');
  const [isNew, setIsNew] = useState(!props.match.params || !props.match.params.id);

  const { roomEntity, checkIns, loading, updating } = props;

  const handleClose = () => {
    props.history.push('/room');
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
        ...roomEntity,
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
          <h2 id="dhHotelApp.room.home.createOrEditLabel">Create or edit a Room</h2>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Loading...</p>
          ) : (
            <AvForm model={isNew ? {} : roomEntity} onSubmit={saveEntity}>
              {!isNew ? (
                <AvGroup>
                  <Label for="room-id">ID</Label>
                  <AvInput id="room-id" type="text" className="form-control" name="id" required readOnly />
                </AvGroup>
              ) : null}
              <AvGroup>
                <Label id="roomTypeLabel" for="room-roomType">
                  Room Type
                </Label>
                <AvInput
                  id="room-roomType"
                  type="select"
                  className="form-control"
                  name="roomType"
                  value={(!isNew && roomEntity.roomType) || 'Single'}
                >
                  <option value="Single">Single</option>
                  <option value="Double">Double</option>
                  <option value="Trible">Trible</option>
                </AvInput>
              </AvGroup>
              <AvGroup>
                <Label id="priceTypeLabel" for="room-priceType">
                  Price Type
                </Label>
                <AvInput
                  id="room-priceType"
                  type="select"
                  className="form-control"
                  name="priceType"
                  value={(!isNew && roomEntity.priceType) || 'Normal'}
                >
                  <option value="Normal">Normal</option>
                </AvInput>
              </AvGroup>
              <AvGroup>
                <Label for="room-checkIn">Check In</Label>
                <AvInput id="room-checkIn" type="select" className="form-control" name="checkIn.id">
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
              <Button tag={Link} id="cancel-save" to="/room" replace color="info">
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
  roomEntity: storeState.room.entity,
  loading: storeState.room.loading,
  updating: storeState.room.updating,
  updateSuccess: storeState.room.updateSuccess,
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

export default connect(mapStateToProps, mapDispatchToProps)(RoomUpdate);
