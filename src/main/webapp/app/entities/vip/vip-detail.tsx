import React, { useEffect } from 'react';
import { connect } from 'react-redux';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { ICrudGetAction } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { IRootState } from 'app/shared/reducers';
import { getEntity } from './vip.reducer';
import { IVip } from 'app/shared/model/vip.model';
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';

export interface IVipDetailProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export const VipDetail = (props: IVipDetailProps) => {
  useEffect(() => {
    props.getEntity(props.match.params.id);
  }, []);

  const { vipEntity } = props;
  return (
    <Row>
      <Col md="8">
        <h2>
          Vip [<b>{vipEntity.id}</b>]
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="phone">Phone</span>
          </dt>
          <dd>{vipEntity.phone}</dd>
          <dt>
            <span id="actived">Actived</span>
          </dt>
          <dd>{vipEntity.actived ? 'true' : 'false'}</dd>
          <dt>Customer</dt>
          <dd>{vipEntity.customer ? vipEntity.customer.id : ''}</dd>
        </dl>
        <Button tag={Link} to="/vip" replace color="info">
          <FontAwesomeIcon icon="arrow-left" /> <span className="d-none d-md-inline">Back</span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/vip/${vipEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Edit</span>
        </Button>
      </Col>
    </Row>
  );
};

const mapStateToProps = ({ vip }: IRootState) => ({
  vipEntity: vip.entity,
});

const mapDispatchToProps = { getEntity };

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(mapStateToProps, mapDispatchToProps)(VipDetail);
