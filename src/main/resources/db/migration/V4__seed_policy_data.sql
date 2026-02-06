DECLARE
v_count NUMBER;
BEGIN

    -- POLICY 1
SELECT COUNT(*) INTO v_count
FROM POLICY
WHERE POLICY_ID = 1;

IF v_count = 0 THEN
    INSERT INTO POLICY (
        POLICY_ID,
        POLICY_NUMBER,
        POLICY_STATUS,
        EFFECTIVE_DATE,
        EXPIRY_DATE,
        CREATED_AT
    ) VALUES (
        1,
        'POL-2025-0001',
        'ACTIVE',
        DATE '2025-01-01',
        DATE '2026-01-01',
        SYSTIMESTAMP
    );
END IF;

    -- POLICY 2
SELECT COUNT(*) INTO v_count
FROM POLICY
WHERE POLICY_ID = 2;

IF v_count = 0 THEN
    INSERT INTO POLICY (
        POLICY_ID,
        POLICY_NUMBER,
        POLICY_STATUS,
        EFFECTIVE_DATE,
        EXPIRY_DATE,
        CREATED_AT
    ) VALUES (
        2,
        'POL-2025-0002',
        'ACTIVE',
        DATE '2025-02-01',
        DATE '2026-02-01',
        SYSTIMESTAMP
    );
END IF;

    -- POLICY 3 (inactive example)
SELECT COUNT(*) INTO v_count
FROM POLICY
WHERE POLICY_ID = 3;

IF v_count = 0 THEN
    INSERT INTO POLICY (
        POLICY_ID,
        POLICY_NUMBER,
        POLICY_STATUS,
        EFFECTIVE_DATE,
        EXPIRY_DATE,
        CREATED_AT
    ) VALUES (
        3,
        'POL-2024-0003',
        'INACTIVE',
        DATE '2024-01-01',
        DATE '2025-01-01',
        SYSTIMESTAMP
    );
END IF;

END;
/
