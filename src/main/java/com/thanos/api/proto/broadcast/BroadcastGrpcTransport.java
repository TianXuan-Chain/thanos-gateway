// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: broadcast.proto

package com.thanos.api.proto.broadcast;

public final class BroadcastGrpcTransport {
  private BroadcastGrpcTransport() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistryLite registry) {
  }

  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
    registerAllExtensions(
        (com.google.protobuf.ExtensionRegistryLite) registry);
  }
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_com_thanos_api_proto_broadcast_DefaultRequest_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_com_thanos_api_proto_broadcast_DefaultRequest_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_com_thanos_api_proto_broadcast_DefaultResponse_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_com_thanos_api_proto_broadcast_DefaultResponse_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_com_thanos_api_proto_broadcast_NodesBroadcastDTO_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_com_thanos_api_proto_broadcast_NodesBroadcastDTO_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_com_thanos_api_proto_broadcast_NodesBroadcastDTO_NodeBroadcastDTO_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_com_thanos_api_proto_broadcast_NodesBroadcastDTO_NodeBroadcastDTO_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_com_thanos_api_proto_broadcast_EthTransactionsBroadcastDTO_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_com_thanos_api_proto_broadcast_EthTransactionsBroadcastDTO_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_com_thanos_api_proto_broadcast_EthTransactionsBroadcastDTO_EthTransactionBroadcastDTO_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_com_thanos_api_proto_broadcast_EthTransactionsBroadcastDTO_EthTransactionBroadcastDTO_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_com_thanos_api_proto_broadcast_GlobalNodeEventsBroadcastDTO_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_com_thanos_api_proto_broadcast_GlobalNodeEventsBroadcastDTO_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_com_thanos_api_proto_broadcast_GlobalNodeEventsBroadcastDTO_GlobalNodeEventBroadcastDTO_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_com_thanos_api_proto_broadcast_GlobalNodeEventsBroadcastDTO_GlobalNodeEventBroadcastDTO_fieldAccessorTable;

  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static  com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    java.lang.String[] descriptorData = {
      "\n\017broadcast.proto\022\036com.thanos.api.proto." +
      "broadcast\"\037\n\016DefaultRequest\022\r\n\005param\030\001 \001" +
      "(\t\"!\n\017DefaultResponse\022\016\n\006result\030\001 \001(\010\"\240\001" +
      "\n\021NodesBroadcastDTO\022Q\n\005nodes\030\001 \003(\0132B.com" +
      ".thanos.api.proto.broadcast.NodesBroadca" +
      "stDTO.NodeBroadcastDTO\0328\n\020NodeBroadcastD" +
      "TO\022\n\n\002id\030\001 \001(\t\022\n\n\002ip\030\002 \001(\t\022\014\n\004port\030\003 \001(\005" +
      "\"\306\001\n\033EthTransactionsBroadcastDTO\022c\n\003txs\030" +
      "\001 \003(\0132V.com.thanos.api.proto.broadcast.E" +
      "thTransactionsBroadcastDTO.EthTransactio" +
      "nBroadcastDTO\032B\n\032EthTransactionBroadcast" +
      "DTO\022\020\n\010fromType\030\001 \001(\005\022\022\n\nrlpEncoded\030\002 \001(" +
      "\014\"\327\001\n\034GlobalNodeEventsBroadcastDTO\022r\n\020gl" +
      "obalNodeEvents\030\001 \003(\0132X.com.thanos.api.pr" +
      "oto.broadcast.GlobalNodeEventsBroadcastD" +
      "TO.GlobalNodeEventBroadcastDTO\032C\n\033Global" +
      "NodeEventBroadcastDTO\022\020\n\010fromType\030\001 \001(\005\022" +
      "\022\n\nrlpEncoded\030\002 \001(\0142\243\003\n\020BroadcastService" +
      "\022\212\001\n\030transportEthTransactions\022;.com.than" +
      "os.api.proto.broadcast.EthTransactionsBr" +
      "oadcastDTO\032/.com.thanos.api.proto.broadc" +
      "ast.DefaultResponse\"\000\022\214\001\n\031transportGloba" +
      "lNodeEvents\022<.com.thanos.api.proto.broad" +
      "cast.GlobalNodeEventsBroadcastDTO\032/.com." +
      "thanos.api.proto.broadcast.DefaultRespon" +
      "se\"\000\022s\n\tfindNodes\0221.com.thanos.api.proto" +
      ".broadcast.NodesBroadcastDTO\0321.com.thano" +
      "s.api.proto.broadcast.NodesBroadcastDTO\"" +
      "\000B:\n\036com.thanos.api.proto.broadcastB\026Bro" +
      "adcastGrpcTransportP\001b\006proto3"
    };
    descriptor = com.google.protobuf.Descriptors.FileDescriptor
      .internalBuildGeneratedFileFrom(descriptorData,
        new com.google.protobuf.Descriptors.FileDescriptor[] {
        });
    internal_static_com_thanos_api_proto_broadcast_DefaultRequest_descriptor =
      getDescriptor().getMessageTypes().get(0);
    internal_static_com_thanos_api_proto_broadcast_DefaultRequest_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_com_thanos_api_proto_broadcast_DefaultRequest_descriptor,
        new java.lang.String[] { "Param", });
    internal_static_com_thanos_api_proto_broadcast_DefaultResponse_descriptor =
      getDescriptor().getMessageTypes().get(1);
    internal_static_com_thanos_api_proto_broadcast_DefaultResponse_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_com_thanos_api_proto_broadcast_DefaultResponse_descriptor,
        new java.lang.String[] { "Result", });
    internal_static_com_thanos_api_proto_broadcast_NodesBroadcastDTO_descriptor =
      getDescriptor().getMessageTypes().get(2);
    internal_static_com_thanos_api_proto_broadcast_NodesBroadcastDTO_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_com_thanos_api_proto_broadcast_NodesBroadcastDTO_descriptor,
        new java.lang.String[] { "Nodes", });
    internal_static_com_thanos_api_proto_broadcast_NodesBroadcastDTO_NodeBroadcastDTO_descriptor =
      internal_static_com_thanos_api_proto_broadcast_NodesBroadcastDTO_descriptor.getNestedTypes().get(0);
    internal_static_com_thanos_api_proto_broadcast_NodesBroadcastDTO_NodeBroadcastDTO_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_com_thanos_api_proto_broadcast_NodesBroadcastDTO_NodeBroadcastDTO_descriptor,
        new java.lang.String[] { "Id", "Ip", "Port", });
    internal_static_com_thanos_api_proto_broadcast_EthTransactionsBroadcastDTO_descriptor =
      getDescriptor().getMessageTypes().get(3);
    internal_static_com_thanos_api_proto_broadcast_EthTransactionsBroadcastDTO_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_com_thanos_api_proto_broadcast_EthTransactionsBroadcastDTO_descriptor,
        new java.lang.String[] { "Txs", });
    internal_static_com_thanos_api_proto_broadcast_EthTransactionsBroadcastDTO_EthTransactionBroadcastDTO_descriptor =
      internal_static_com_thanos_api_proto_broadcast_EthTransactionsBroadcastDTO_descriptor.getNestedTypes().get(0);
    internal_static_com_thanos_api_proto_broadcast_EthTransactionsBroadcastDTO_EthTransactionBroadcastDTO_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_com_thanos_api_proto_broadcast_EthTransactionsBroadcastDTO_EthTransactionBroadcastDTO_descriptor,
        new java.lang.String[] { "FromType", "RlpEncoded", });
    internal_static_com_thanos_api_proto_broadcast_GlobalNodeEventsBroadcastDTO_descriptor =
      getDescriptor().getMessageTypes().get(4);
    internal_static_com_thanos_api_proto_broadcast_GlobalNodeEventsBroadcastDTO_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_com_thanos_api_proto_broadcast_GlobalNodeEventsBroadcastDTO_descriptor,
        new java.lang.String[] { "GlobalNodeEvents", });
    internal_static_com_thanos_api_proto_broadcast_GlobalNodeEventsBroadcastDTO_GlobalNodeEventBroadcastDTO_descriptor =
      internal_static_com_thanos_api_proto_broadcast_GlobalNodeEventsBroadcastDTO_descriptor.getNestedTypes().get(0);
    internal_static_com_thanos_api_proto_broadcast_GlobalNodeEventsBroadcastDTO_GlobalNodeEventBroadcastDTO_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_com_thanos_api_proto_broadcast_GlobalNodeEventsBroadcastDTO_GlobalNodeEventBroadcastDTO_descriptor,
        new java.lang.String[] { "FromType", "RlpEncoded", });
  }

  // @@protoc_insertion_point(outer_class_scope)
}