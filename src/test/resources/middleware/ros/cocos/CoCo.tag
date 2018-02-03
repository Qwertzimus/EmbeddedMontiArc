package middleware.ros.cocos;
conforms to middleware.ros.RosToEmamTagSchema;

tags CoCo{
//Valid
tag RosToRosComp.SubComp1.outPort with
RosConnection = {topic = (/clock, rosgraph_msgs/Clock), msgField = clock.toSec()};

tag RosToRosComp.SubComp2.inPort with
RosConnection = {topic = (/clock, rosgraph_msgs/Clock), msgField = clock.toSec()};

//Invalid: topic name mismatch
tag TopicNameMismatch.SubComp1.outPort with
RosConnection = {topic = (/clock, rosgraph_msgs/Clock), msgField = clock.toSec()};

tag TopicNameMismatch.SubComp2.inPort with
RosConnection = {topic = (/echo, rosgraph_msgs/Clock), msgField = clock.toSec()};

//Invalid: topic type mismatch
tag TopicTypeMismatch.SubComp1.outPort with
RosConnection = {topic = (/clock, rosgraph_msgs/Clock), msgField = clock.toSec()};

tag TopicTypeMismatch.SubComp2.inPort with
RosConnection = {topic = (/clock, rosgraph_msgs/Clock1), msgField = clock.toSec()};

//Invalid: nonRos to Ros
tag NoRosToRosComp.SubComp2.inPort with
RosConnection = {topic = (/clock, rosgraph_msgs/Clock), msgField = clock.toSec()};


}