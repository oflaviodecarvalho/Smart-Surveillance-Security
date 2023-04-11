package org.flavio.server;

import io.grpc.stub.StreamObserver;



public class AccessMonitoringImpl extends AccessMonitoringGrpc.AccessMonitoringImplBase {

    @Override
    public void ringAudibleAlarm(RingAlarmRequest request, StreamObserver<RingAlarmResponse> responseObserver) {
        RingAlarmResponse ringAlarmResponse= RingAlarmResponse.newBuilder()
                .setRing(true)
                .build();
        responseObserver.onNext(ringAlarmResponse);
        responseObserver.onCompleted();
    }

    @Override
    public void setPerimeterLevel(SetPerimeterLevelRequest request, StreamObserver<SetPerimeterLevelResponse> responseObserver) {
        super.setPerimeterLevel(request, responseObserver);
    }
}
