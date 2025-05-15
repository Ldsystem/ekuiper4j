package cn.brk2outside.ekuiper4j.constants;

public interface Endpoints {

    interface Overview {
        String GET = "";
        String PING = "ping";
    }

    interface Stream {
        String CREATE_STREAM_ENDPOINT = "/streams";
        String LIST_STREAM_ENDPOINT = CREATE_STREAM_ENDPOINT;
        String GET_STREAM_DETAILS = "/streamdetails";
        String GET_STREAM_DETAIL = "/streams/{id}";
        String GET_STREAM_SCHEMA = "/streams/{id}/schema";
        String UPDATE_STREAM = "/streams/{id}";
        String DELETE_STREAM = UPDATE_STREAM;
    }

    interface ConfigKey {
        String LIST_MQTT_BROKERS = "/metadata/sources/yaml/mqtt";
        String DELETE_MQTT_BROKER = "/metadata/sources/mqtt/confKeys/{confKey}";
        String PUT_MQTT_BROKER = "/metadata/sources/mqtt/confKeys/{confKey}";
    }

    interface Connection {
        String CREATE_CONNECTION = "/connections";
        String UPDATE_CONNECTION = "/connection/{id}";
        String LIST_CONNECTIONS = "/connections";
        String GET_CONNECTION_STATUS = "/connections/{id}";
        String DELETE_CONNECTION = GET_CONNECTION_STATUS;
        String SINK_CONNECTION_CHECK = "/metadata/sinks/connection/{type}";
        String SOURCE_CONNECTION_CHECK = "/metadata/source/connection/{type}";
        String MQTT_SOURCE_CONNECTION_CHECK = "/metadata/source/connection/mqtt";
    }

    interface Rules {
        String CREATE_RULE = "/rules";
        String LIST_RULES = CREATE_RULE;
        String GET_RULE = "/rules/{id}";
        String UPDATE_RULE = GET_RULE;
        String DELETE_RULE = GET_RULE;
        String START_RULE = "/rules/{id}/start";
        String STOP_RULE = "/rules/{id}/stop";
        String RESTART_RULE = "/rules/{id}/restart";
        String GET_RULE_STATUS = "/rules/{id}/status";
        String GET_ALL_RULES_STATUS = "/rules/status/all";
        String VALIDATE_RULE = "/rules/validate";
        String EXPLAIN_RULE = "/rules/{id}/explain";
        String GET_RULES_CPU_USAGE = "/rules/usage/cpu";
    }
}
