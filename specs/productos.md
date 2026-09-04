# Features: Gestión de Productos

## Descripción general

El administrador gestiona los productos del sistema. Cada producto pertenece
a una categoría, tiene un precio y un stock disponible.

---

## Crear un producto

```gherkin
Feature: Crear un producto

  Scenario: El administrador registra un producto nuevo
    Given la categoría "Electrónica" existe con id 1
    When el administrador envía una solicitud para crear un producto con:
      | nombre          | precio | stock | categoriaId |
      | iPhone 15       | 999.99 | 50    | 1           |
    Then el sistema confirma la creación del producto
         y el producto "iPhone 15" aparece en el listado
         y tiene un precio de 999.99 y stock de 50

  Scenario: El administrador intenta crear un producto sin categoría
    Given no existe una categoría con id 999
    When el administrador envía una solicitud para crear un producto con:
      | nombre    | precio | stock | categoriaId |
      | Audífonos | 49.99  | 100   | 999         |
    Then el sistema informa que la categoría no fue encontrada
         y no se crea el producto

  Scenario: El administrador intenta crear un producto con precio negativo
    Given la categoría "Electrónica" existe con id 1
    When el administrador envía una solicitud para crear un producto con:
      | nombre    | precio | stock | categoriaId |
      | Mouses    | -10.00 | 25    | 1           |
    Then el sistema indica que el precio no puede ser negativo
         y no se crea el producto

  Scenario: El administrador intenta crear un producto con stock negativo
    Given la categoría "Electrónica" existe con id 1
    When el administrador envía una solicitud para crear un producto con:
      | nombre    | precio | stock | categoriaId |
      | Teclados  | 29.99  | -5    | 1           |
    Then el sistema indica que el stock no puede ser negativo
         y no se crea el producto
```

---

## Consultar productos

```gherkin
Feature: Consultar productos

  Scenario: El administrador consulta todos los productos
    Given existen productos registrados
    When el administrador solicita ver todos los productos
    Then el sistema muestra el listado completo de productos

  Scenario: El administrador consulta los datos de un producto específico
    Given el producto "iPhone 15" está registrado con id 1
    When el administrador solicita ver los datos del producto 1
    Then el sistema muestra sus datos completos
         y su precio es 999.99 y su stock es 50

  Scenario: El administrador consulta un producto que no existe
    Given el producto con identificación "999" no está registrado
    When el administrador solicita ver los datos del producto "999"
    Then el sistema informa que el producto no fue encontrado
```

---

## Actualizar un producto

```gherkin
Feature: Actualizar un producto

  Scenario: El administrador actualiza los datos de un producto existente
    Given el producto "iPhone 15" está registrado con id 1
    When el administrador envía una solicitud para actualizar el producto 1 con:
      | nombre            | precio  | stock | categoriaId |
      | iPhone 15 Pro Max | 1199.99 | 30    | 1           |
    Then el sistema confirma la actualización
         y el producto ahora se llama "iPhone 15 Pro Max"
         y su nuevo precio es 1199.99

  Scenario: El administrador intenta actualizar un producto que no existe
    Given el producto con identificación "999" no está registrado
    When el administrador intenta actualizar los datos del producto "999"
    Then el sistema informa que el producto no fue encontrado
         y no se realiza ninguna actualización

  Scenario: El administrador intenta actualizar un producto con categoría inexistente
    Given el producto "iPhone 15" está registrado con id 1
    When el administrador intenta cambiar la categoría del producto 1 a la categoría "999"
    Then el sistema informa que la categoría no fue encontrada
         y no se realiza la actualización
```

---

## Eliminar un producto

```gherkin
Feature: Eliminar un producto

  Scenario: El administrador elimina un producto existente
    Given el producto "iPhone 15" está registrado con id 1
    When el administrador elimina el producto 1
    Then el sistema confirma la eliminación
         y el producto ya no aparece en el listado

  Scenario: El administrador intenta eliminar un producto que no existe
    Given el producto con identificación "999" no está registrado
    When el administrador intenta eliminar el producto "999"
    Then el sistema informa que el producto no fue encontrado
         y no se realiza ninguna eliminación
```

---

## Actualizar stock

```gherkin
Feature: Actualizar stock de un producto

  Scenario: El administrador actualiza el stock de un producto
    Given el producto "iPhone 15" está registrado con id 1 y stock 50
    When el administrador envía una solicitud para actualizar el stock del producto 1 a 75
    Then el sistema confirma la actualización del stock
         y el producto ahora tiene stock de 75

  Scenario: El administrador intenta poner stock negativo
    Given el producto "iPhone 15" está registrado con id 1 y stock 50
    When el administrador intenta actualizar el stock del producto 1 a -10
    Then el sistema indica que el stock no puede ser negativo
         y no se realiza la actualización
```

---

## Notas
- Los IDs de producto son internos del sistema y se asignan automáticamente.
- El precio y stock deben ser valores numéricos positivos (o cero para stock).
- Cada producto pertenece obligatoriamente a una categoría existente.
