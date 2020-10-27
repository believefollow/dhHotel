import React, { useEffect } from 'react';
import { connect } from 'react-redux';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { ICrudGetAction } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { IRootState } from 'app/shared/reducers';
import { getEntity } from './room.reducer';
import { IRoom } from 'app/shared/model/room.model';
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';

export interface IRoomDetailProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export const RoomDetail = (props: IRoomDetailProps) => {
  useEffect(() => {
    props.getEntity(props.match.params.id);
  }, []);

  const { roomEntity } = props;
  return (
    <Row>
      <Col md="8">
        <h2>
          Room [<b>{roomEntity.id}</b>]
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="roomType">Room Type</span>
          </dt>
          <dd>{roomEntity.roomType}</dd>
          <dt>
            <span id="priceType">Price Type</span>
          </dt>
          <dd>{roomEntity.priceType}</dd>
          <dt>Check In</dt>
          <dd>{roomEntity.checkIn ? roomEntity.checkIn.id : ''}</dd>
        </dl>
        <Button tag={Link} to="/room" replace color="info">
          <FontAwesomeIcon icon="arrow-left" /> <span className="d-none d-md-inline">Back</span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/room/${roomEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Edit</span>
        </Button>
      </Col>
    </Row>
  );
};

const mapStateToProps = ({ room }: IRootState) => ({
  roomEntity: room.entity,
});

const mapDispatchToProps = { getEntity };

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(mapStateToProps, mapDispatchToProps)(RoomDetail);
