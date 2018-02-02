package middleware.ros;
conforms to middleware.ros.RosToEmamTagSchema;

tags OptionalMsgField{
tag optionalMsgField.in1 with RosConnection = {topic = (name1, package/topic1)};
tag optionalMsgField.out1 with RosConnection = {topic = (name1, package/topic1) , msgField = msgField1};
}