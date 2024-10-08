// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: push.proto

package com.thanos.api.proto.push;

public final class PushGrpcTransport {
  private PushGrpcTransport() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistryLite registry) {
  }

  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
    registerAllExtensions(
        (com.google.protobuf.ExtensionRegistryLite) registry);
  }
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_com_thanos_api_proto_push_DefaultRequest_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_com_thanos_api_proto_push_DefaultRequest_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_com_thanos_api_proto_push_DefaultResponse_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_com_thanos_api_proto_push_DefaultResponse_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_com_thanos_api_proto_push_LongObject_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_com_thanos_api_proto_push_LongObject_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_com_thanos_api_proto_push_StringObject_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_com_thanos_api_proto_push_StringObject_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_com_thanos_api_proto_push_ListBytesObject_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_com_thanos_api_proto_push_ListBytesObject_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_com_thanos_api_proto_push_BytesObject_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_com_thanos_api_proto_push_BytesObject_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_com_thanos_api_proto_push_BlockBytesObject_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_com_thanos_api_proto_push_BlockBytesObject_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_com_thanos_api_proto_push_EthTransactionsPushDTO_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_com_thanos_api_proto_push_EthTransactionsPushDTO_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_com_thanos_api_proto_push_EthTransactionsPushDTO_EthTransactionPushDTO_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_com_thanos_api_proto_push_EthTransactionsPushDTO_EthTransactionPushDTO_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_com_thanos_api_proto_push_GlobalNodeEventsPushDTO_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_com_thanos_api_proto_push_GlobalNodeEventsPushDTO_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_com_thanos_api_proto_push_GlobalNodeEventsPushDTO_GlobalNodeEventPushDTO_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_com_thanos_api_proto_push_GlobalNodeEventsPushDTO_GlobalNodeEventPushDTO_fieldAccessorTable;

  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static  com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    java.lang.String[] descriptorData = {
      "\n\npush.proto\022\031com.thanos.api.proto.push\"" +
      "\035\n\016DefaultRequest\022\013\n\003req\030\001 \001(\t\"!\n\017Defaul" +
      "tResponse\022\016\n\006result\030\001 \001(\010\"\033\n\nLongObject\022" +
      "\r\n\005value\030\001 \001(\003\"\035\n\014StringObject\022\r\n\005value\030" +
      "\001 \001(\t\" \n\017ListBytesObject\022\r\n\005value\030\001 \003(\014\"" +
      "\034\n\013BytesObject\022\r\n\005value\030\001 \001(\014\")\n\020BlockBy" +
      "tesObject\022\025\n\rblockBaseInfo\030\001 \001(\014\"\352\002\n\026Eth" +
      "TransactionsPushDTO\022T\n\003txs\030\001 \003(\0132G.com.t" +
      "hanos.api.proto.push.EthTransactionsPush" +
      "DTO.EthTransactionPushDTO\032\371\001\n\025EthTransac" +
      "tionPushDTO\022\021\n\tpublicKey\030\001 \001(\014\022\r\n\005nonce\030" +
      "\002 \001(\014\022\031\n\021futureEventNumber\030\003 \001(\003\022\020\n\010gasP" +
      "rice\030\004 \001(\014\022\020\n\010gasLimit\030\005 \001(\014\022\026\n\016receiveA" +
      "ddress\030\006 \001(\014\022\r\n\005value\030\007 \001(\014\022\014\n\004data\030\010 \001(" +
      "\014\022\025\n\rexecuteStates\030\t \003(\014\022\021\n\tsignature\030\n " +
      "\001(\014\022\014\n\004hash\030\013 \001(\014\022\022\n\nrlpEncoded\030\014 \001(\014\"\217\002" +
      "\n\027GlobalNodeEventsPushDTO\022X\n\005nodes\030\001 \003(\013" +
      "2I.com.thanos.api.proto.push.GlobalNodeE" +
      "ventsPushDTO.GlobalNodeEventPushDTO\032\231\001\n\026" +
      "GlobalNodeEventPushDTO\022\014\n\004hash\030\001 \001(\014\022\021\n\t" +
      "publicKey\030\002 \001(\014\022\r\n\005nonce\030\003 \001(\014\022\031\n\021future" +
      "EventNumber\030\004 \001(\003\022\023\n\013commandCode\030\005 \001(\005\022\014" +
      "\n\004data\030\006 \001(\014\022\021\n\tsignature\030\007 \001(\0142\243\014\n\013Push" +
      "Service\022d\n\tsaveAlive\022).com.thanos.api.pr" +
      "oto.push.DefaultRequest\032*.com.thanos.api" +
      ".proto.push.DefaultResponse\"\000\022v\n\023pushEth" +
      "Transactions\0221.com.thanos.api.proto.push" +
      ".EthTransactionsPushDTO\032*.com.thanos.api" +
      ".proto.push.DefaultResponse\"\000\022x\n\024pushGlo" +
      "balNodeEvents\0222.com.thanos.api.proto.pus" +
      "h.GlobalNodeEventsPushDTO\032*.com.thanos.a" +
      "pi.proto.push.DefaultResponse\"\000\022f\n\022getGl" +
      "obalNodeEvent\022&.com.thanos.api.proto.pus" +
      "h.BytesObject\032&.com.thanos.api.proto.pus" +
      "h.BytesObject\"\000\022m\n\031getGlobalNodeEventRec" +
      "eipt\022&.com.thanos.api.proto.push.BytesOb" +
      "ject\032&.com.thanos.api.proto.push.BytesOb" +
      "ject\"\000\022d\n\rgetEpochState\022).com.thanos.api" +
      ".proto.push.DefaultRequest\032&.com.thanos." +
      "api.proto.push.BytesObject\"\000\022l\n\026getLates" +
      "tBeExecutedNum\022).com.thanos.api.proto.pu" +
      "sh.DefaultRequest\032%.com.thanos.api.proto" +
      ".push.LongObject\"\000\022n\n\030getLatestConsensus" +
      "Number\022).com.thanos.api.proto.push.Defau" +
      "ltRequest\032%.com.thanos.api.proto.push.Lo" +
      "ngObject\"\000\022k\n\025getCurrentCommitRound\022).co" +
      "m.thanos.api.proto.push.DefaultRequest\032%" +
      ".com.thanos.api.proto.push.LongObject\"\000\022" +
      "k\n\027getEthTransactionByHash\022&.com.thanos." +
      "api.proto.push.BytesObject\032&.com.thanos." +
      "api.proto.push.BytesObject\"\000\022v\n\032getEthTr" +
      "ansactionsByHashes\022*.com.thanos.api.prot" +
      "o.push.ListBytesObject\032*.com.thanos.api." +
      "proto.push.ListBytesObject\"\000\022h\n\020getBlock" +
      "ByNumber\022%.com.thanos.api.proto.push.Lon" +
      "gObject\032+.com.thanos.api.proto.push.Bloc" +
      "kBytesObject\"\000\022g\n\024getEventDataByNumber\022%" +
      ".com.thanos.api.proto.push.LongObject\032&." +
      "com.thanos.api.proto.push.BytesObject\"\000\022" +
      "|\n\007ethCall\022G.com.thanos.api.proto.push.E" +
      "thTransactionsPushDTO.EthTransactionPush" +
      "DTO\032&.com.thanos.api.proto.push.BytesObj" +
      "ect\"\000B0\n\031com.thanos.api.proto.pushB\021Push" +
      "GrpcTransportP\001b\006proto3"
    };
    descriptor = com.google.protobuf.Descriptors.FileDescriptor
      .internalBuildGeneratedFileFrom(descriptorData,
        new com.google.protobuf.Descriptors.FileDescriptor[] {
        });
    internal_static_com_thanos_api_proto_push_DefaultRequest_descriptor =
      getDescriptor().getMessageTypes().get(0);
    internal_static_com_thanos_api_proto_push_DefaultRequest_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_com_thanos_api_proto_push_DefaultRequest_descriptor,
        new java.lang.String[] { "Req", });
    internal_static_com_thanos_api_proto_push_DefaultResponse_descriptor =
      getDescriptor().getMessageTypes().get(1);
    internal_static_com_thanos_api_proto_push_DefaultResponse_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_com_thanos_api_proto_push_DefaultResponse_descriptor,
        new java.lang.String[] { "Result", });
    internal_static_com_thanos_api_proto_push_LongObject_descriptor =
      getDescriptor().getMessageTypes().get(2);
    internal_static_com_thanos_api_proto_push_LongObject_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_com_thanos_api_proto_push_LongObject_descriptor,
        new java.lang.String[] { "Value", });
    internal_static_com_thanos_api_proto_push_StringObject_descriptor =
      getDescriptor().getMessageTypes().get(3);
    internal_static_com_thanos_api_proto_push_StringObject_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_com_thanos_api_proto_push_StringObject_descriptor,
        new java.lang.String[] { "Value", });
    internal_static_com_thanos_api_proto_push_ListBytesObject_descriptor =
      getDescriptor().getMessageTypes().get(4);
    internal_static_com_thanos_api_proto_push_ListBytesObject_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_com_thanos_api_proto_push_ListBytesObject_descriptor,
        new java.lang.String[] { "Value", });
    internal_static_com_thanos_api_proto_push_BytesObject_descriptor =
      getDescriptor().getMessageTypes().get(5);
    internal_static_com_thanos_api_proto_push_BytesObject_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_com_thanos_api_proto_push_BytesObject_descriptor,
        new java.lang.String[] { "Value", });
    internal_static_com_thanos_api_proto_push_BlockBytesObject_descriptor =
      getDescriptor().getMessageTypes().get(6);
    internal_static_com_thanos_api_proto_push_BlockBytesObject_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_com_thanos_api_proto_push_BlockBytesObject_descriptor,
        new java.lang.String[] { "BlockBaseInfo", });
    internal_static_com_thanos_api_proto_push_EthTransactionsPushDTO_descriptor =
      getDescriptor().getMessageTypes().get(7);
    internal_static_com_thanos_api_proto_push_EthTransactionsPushDTO_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_com_thanos_api_proto_push_EthTransactionsPushDTO_descriptor,
        new java.lang.String[] { "Txs", });
    internal_static_com_thanos_api_proto_push_EthTransactionsPushDTO_EthTransactionPushDTO_descriptor =
      internal_static_com_thanos_api_proto_push_EthTransactionsPushDTO_descriptor.getNestedTypes().get(0);
    internal_static_com_thanos_api_proto_push_EthTransactionsPushDTO_EthTransactionPushDTO_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_com_thanos_api_proto_push_EthTransactionsPushDTO_EthTransactionPushDTO_descriptor,
        new java.lang.String[] { "PublicKey", "Nonce", "FutureEventNumber", "GasPrice", "GasLimit", "ReceiveAddress", "Value", "Data", "ExecuteStates", "Signature", "Hash", "RlpEncoded", });
    internal_static_com_thanos_api_proto_push_GlobalNodeEventsPushDTO_descriptor =
      getDescriptor().getMessageTypes().get(8);
    internal_static_com_thanos_api_proto_push_GlobalNodeEventsPushDTO_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_com_thanos_api_proto_push_GlobalNodeEventsPushDTO_descriptor,
        new java.lang.String[] { "Nodes", });
    internal_static_com_thanos_api_proto_push_GlobalNodeEventsPushDTO_GlobalNodeEventPushDTO_descriptor =
      internal_static_com_thanos_api_proto_push_GlobalNodeEventsPushDTO_descriptor.getNestedTypes().get(0);
    internal_static_com_thanos_api_proto_push_GlobalNodeEventsPushDTO_GlobalNodeEventPushDTO_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_com_thanos_api_proto_push_GlobalNodeEventsPushDTO_GlobalNodeEventPushDTO_descriptor,
        new java.lang.String[] { "Hash", "PublicKey", "Nonce", "FutureEventNumber", "CommandCode", "Data", "Signature", });
  }

  // @@protoc_insertion_point(outer_class_scope)
}
