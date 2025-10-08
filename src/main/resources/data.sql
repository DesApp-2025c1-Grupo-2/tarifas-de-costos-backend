
INSERT INTO Provincias (nombre, activo)
SELECT * FROM (
    SELECT 'Buenos Aires' AS nombre, true AS activo UNION ALL
    SELECT 'Catamarca', true UNION ALL
    SELECT 'Chaco', true UNION ALL
    SELECT 'Chubut', true UNION ALL
    SELECT 'Ciudad Autónoma de Buenos Aires', true UNION ALL
    SELECT 'Córdoba', true UNION ALL
    SELECT 'Corrientes', true UNION ALL
    SELECT 'Entre Ríos', true UNION ALL
    SELECT 'Formosa', true UNION ALL
    SELECT 'Jujuy', true UNION ALL
    SELECT 'La Pampa', true UNION ALL
    SELECT 'La Rioja', true UNION ALL
    SELECT 'Mendoza', true UNION ALL
    SELECT 'Misiones', true UNION ALL
    SELECT 'Neuquén', true UNION ALL
    SELECT 'Río Negro', true UNION ALL
    SELECT 'Salta', true UNION ALL
    SELECT 'San Juan', true UNION ALL
    SELECT 'San Luis', true UNION ALL
    SELECT 'Santa Cruz', true UNION ALL
    SELECT 'Santa Fe', true UNION ALL
    SELECT 'Santiago del Estero', true UNION ALL
    SELECT 'Tierra del Fuego', true UNION ALL
    SELECT 'Tucumán', true UNION ALL
    SELECT 'AMBA', true
) AS tmp
WHERE NOT EXISTS (SELECT 1 FROM Provincias);