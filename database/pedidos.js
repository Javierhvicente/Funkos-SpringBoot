db.createUser({
    user: 'javier',
    pwd: 'javier123',
    roles: [
        {
            role: 'readWrite',
            db: 'funkos',
        },
    ],
});


db = db.getSiblingDB('funkos');

db.createCollection('pedidos');

db.pedidos.insertMany([
    {
        _id: ObjectId('6536518de9b0d305f193b5ef'),
        idUsuario: 1,
        cliente: {
            nombreCompleto: 'Torrente',
            email: 'torrente@gmail.com',
            telefono: '+34123456789',
            direccion: {
                calle: 'vicente calderon',
                numero: '10',
                ciudad: 'Madrid',
                provincia: 'Madrid',
                pais: 'España',
                codigoPostal: '28001',
            },
        },
        lineasPedido: [
            {
                idProducto: 1,
                precioProducto: 19.99,
                cantidad: 1,
                total: 19.99,
            },
            {
                idProducto: 2,
                precioProducto: 15.99,
                cantidad: 2,
                total: 31.98,
            },
        ],
        createdAt: '2023-10-23T12:57:17.3411925',
        updatedAt: '2023-10-23T12:57:17.3411925',
        isDeleted: false,
        totalItems: 3,
        total: 51.97,
        _class: 'Pedido',
    },
]);
