import React, { useEffect } from 'react';
import { connect } from 'react-redux';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { ICrudGetAction, TextFormat } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { IRootState } from 'app/shared/reducers';
import { getEntity } from './check-in.reducer';
import { ICheckIn } from 'app/shared/model/check-in.model';
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';

export interface ICheckInDetailProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export const CheckInDetail = (props: ICheckInDetailProps) => {
  useEffect(() => {
    props.getEntity(props.match.params.id);
  }, []);

  const { checkInEntity } = props;
  return (
    <Row>
      <Col md="8">
        <h2>
          CheckIn [<b>{checkInEntity.id}</b>]
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="startTime">Start Time</span>
          </dt>
          <dd>
            {checkInEntity.startTime ? <TextFormat value={checkInEntity.startTime} type="date" format={APP_LOCAL_DATE_FORMAT} /> : null}
          </dd>
          <dt>
            <span id="endTime">End Time</span>
          </dt>
          <dd>{checkInEntity.endTime ? <TextFormat value={checkInEntity.endTime} type="date" format={APP_LOCAL_DATE_FORMAT} /> : null}</dd>
        </dl>
        <Button tag={Link} to="/check-in" replace color="info">
          <FontAwesomeIcon icon="arrow-left" /> <span className="d-none d-md-inline">Back</span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/check-in/${checkInEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Edit</span>
        </Button>
      </Col>
    </Row>
  );
};

const mapStateToProps = ({ checkIn }: IRootState) => ({
  checkInEntity: checkIn.entity,
});

const mapDispatchToProps = { getEntity };

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(mapStateToProps, mapDispatchToProps)(CheckInDetail);
