CREATE TABLE workflow_definition
(
    id UUID PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    definition_version INTEGER NOT NULL,
    description VARCHAR(500),
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL,
    version BIGINT NOT NULL DEFAULT 0,

    CONSTRAINT uk_name_version
        UNIQUE(name, version)
);

CREATE TABLE workflow_step
(
    id UUID PRIMARY KEY,
    workflow_definition_id UUID NOT NULL,
    sequence_number INTEGER NOT NULL,
    activity_name VARCHAR(100) NOT NULL,
    timeout_seconds INTEGER,
    version BIGINT NOT NULL DEFAULT 0,

    CHECK (sequence_number > 0),

    CHECK (timeout_seconds IS NULL OR timeout_seconds > 0),

    CONSTRAINT fk_workflow_step_workflow_definition_id
        FOREIGN KEY (workflow_definition_id)
        REFERENCES workflow_definition(id),

    CONSTRAINT uk_workflow_step_sequence
        UNIQUE(workflow_definition_id, sequence_number)
);

CREATE TABLE workflow_execution
(
    id UUID PRIMARY KEY,
    workflow_definition_id UUID NOT NULL,
    input JSONB,
    status VARCHAR(20) NOT NULL,
    started_at TIMESTAMPTZ NOT NULL,
    completed_at TIMESTAMPTZ,
    version BIGINT NOT NULL DEFAULT 0,

    CONSTRAINT fk_workflow_execution_workflow_definition_id
        FOREIGN KEY (workflow_definition_id)
        REFERENCES workflow_definition(id)
);

CREATE TABLE step_execution
(
    id UUID PRIMARY KEY,
    workflow_execution_id UUID NOT NULL,
    workflow_step_id UUID NOT NULL,
    status VARCHAR(20) NOT NULL,
    retry_count INTEGER NOT NULL DEFAULT 0,
    started_at TIMESTAMPTZ NOT NULL,
    completed_at TIMESTAMPTZ,
    error_message TEXT,
    version BIGINT NOT NULL DEFAULT 0,

    CONSTRAINT fk_step_execution_workflow_execution_id
        FOREIGN KEY (workflow_execution_id)
        REFERENCES workflow_execution(id),

    CONSTRAINT fk_step_execution_workflow_step_id
        FOREIGN KEY (workflow_step_id)
        REFERENCES workflow_step(id)
);

