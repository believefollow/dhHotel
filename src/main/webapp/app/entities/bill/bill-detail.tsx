import React, { useEffect } from 'react';
import { connect } from 'react-redux';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { ICrudGetAction } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { IRootState } from 'app/shared/reducers';
import { getEntity } from './bill.reducer';
import { IBill } from 'app/shared/model/bill.model';
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';

export interface IBillDetailProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export const BillDetail = (props: IBillDetailProps) => {
  useEffect(() => {
    props.getEntity(props.match.params.id);
  }, []);

  const { billEntity } = props;
  return (
    <Row>
      <Col md="8">
        <h2>
          Bill [<b>{billEntity.id}</b>]
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="balance">Balance</span>
          </dt>
          <dd>{billEntity.balance}</dd>
          <dt>
            <span id="source">Source</span>
          </dt>
          <dd>{billEntity.source}</dd>
          <dt>Check In</dt>
          <dd>{billEntity.checkIn ? billEntity.checkIn.id : ''}</dd>
        </dl>
        <Button tag={Link} to="/bill" replace color="info">
          <FontAwesomeIcon icon="arrow-left" /> <span className="d-none d-md-inline">Back</span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/bill/${billEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Edit</span>
        </Button>
      </Col>
    </Row>
  );
};

const mapStateToProps = ({ bill }: IRootState) => ({
  billEntity: bill.entity,
});

const mapDispatchToProps = { getEntity };

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(mapStateToProps, mapDispatchToProps)(BillDetail);
