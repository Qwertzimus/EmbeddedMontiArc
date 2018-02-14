package middleware.ros;
conforms to middleware.ros.RosToEmamTagSchema;

tags Echo {
tag basicParsing.rosIn with RosConnection = {topic = (/clock, rosgraph_msgs/Clock), msgField = clock.toSec()};
tag basicParsing.rosOut with RosConnection = {topic = (/echo, automated_driving_msgs/StampedFloat64), msgField = data};
tag basicParsing.emptyTagIn with RosConnection;
}