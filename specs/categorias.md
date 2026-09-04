# Features: Gestión de Categorías

## Descripción general

El administrador gestiona las categorías de productos del sistema. Las categorías
soportan una estructura jerárquica (árbol), donde cada categoría puede tener
una categoría padre y múltiples subcategorías.

---

## Crear una categoría

```gherkin
Feature: Crear una categoría

  Scenario: El administrador crea una categoría raíz
    Given no existe una categoría con el nombre "Electrónica"
    When el administrador envía una solicitud para crear una categoría con:
      | nombre       | parentId |
      | Electrónica  |          |
    Then el sistema confirma la creación de la categoría
         y "Electrónica" aparece en el listado de categorías raíz

  Scenario: El administrador crea una subcategoría
    Given la categoría "Electrónica" existe con id 1
    When el administrador envía una solicitud para crear una categoría con:
      | nombre     | parentId |
      | Celulares  | 1        |
    Then el sistema confirma la creación de la subcategoría
         y "Celulares" es hija de "Electrónica"

  Scenario: El administrador intenta crear una categoría con nombre duplicado bajo el mismo padre
    Given la categoría "Electrónica" existe con id 1
    And existe una subcategoría "Celulares" bajo "Electrónica"
    When el administrador envía una solicitud para crear una categoría con:
      | nombre     | parentId |
      | Celulares  | 1        |
    Then el sistema informa que ya existe una categoría con ese nombre bajo el mismo padre
         y no se crea la categoría
```

---

## Consultar categorías

```gherkin
Feature: Consultar categorías

  Background:
    Given existen categorías registradas:
      | nombre       | parentId |
      | Electrónica  |          |
      | Celulares    | 1        |
      | Ropa         |          |

  Scenario: El administrador consulta todas las categorías en formato árbol
    When el administrador solicita ver todas las categorías
    Then el sistema retorna las categorías con su estructura jerárquica
         y "Electrónica" tiene "Celulares" como subcategoría
         y "Ropa" aparece como categoría raíz

  Scenario: El administrador consulta los datos de una categoría específica
    When el administrador solicita ver los datos de la categoría 1
    Then el sistema muestra sus datos completos
         y incluye el listado de subcategorías directas

  Scenario: El administrador consulta una categoría que no existe
    When el administrador solicita ver los datos de la categoría 999
    Then el sistema informa que la categoría no fue encontrada
```

---

## Actualizar una categoría

```gherkin
Feature: Actualizar una categoría

  Background:
    Given la categoría "Electrónica" existe con id 1

  Scenario: El administrador actualiza el nombre de una categoría
    When el administrador envía una solicitud para actualizar la categoría 1 con:
      | nombre              |
      | Tecnología          |
    Then el sistema confirma la actualización
         y la categoría ahora se llama "Tecnología"

  Scenario: El administrador intenta actualizar una categoría con nombre duplicado
    Given la categoría "Tecnología" existe con id 2
    When el administrador intenta cambiar el nombre de la categoría 1 a "Tecnología"
    Then el sistema informa que ya existe una categoría con ese nombre
         y no se realiza la actualización

  Scenario: El administrador intenta actualizar una categoría que no existe
    When el administrador intenta actualizar los datos de la categoría 999
    Then el sistema informa que la categoría no fue encontrada
```

---

## Eliminar una categoría

```gherkin
Feature: Eliminar una categoría

  Scenario: El administrador elimina una categoría sin subcategorías ni productos
    Given la categoría "Oficina" existe con id 5
    And la categoría "Oficina" no tiene subcategorías
    And la categoría "Oficina" no tiene productos asociados
    When el administrador elimina la categoría 5
    Then el sistema confirma la eliminación
         y la categoría ya no aparece en el listado

  Scenario Outline: El administrador intenta eliminar una categoría con dependencias
    Given la categoría <nombre> existe con id <id>
    And la categoría <nombre> tiene <dependencia>
    When el administrador intenta eliminar la categoría <id>
    Then el sistema informa que no se puede eliminar porque tiene <dependencia>
         y no se realiza la eliminación

    Examples:
      | nombre      | id | dependencia              |
      | Electrónica | 1  | subcategorías            |
      | Electrónica | 1  | productos asociados      |

  Scenario: El administrador intenta eliminar una categoría que no existe
    When el administrador intenta eliminar la categoría 999
    Then el sistema informa que la categoría no fue encontrada
```

---

## Notas
- Los IDs de categoría son internos del sistema y se asignan automáticamente.
- Una categoría raíz no tiene parentId (es null o vacío).
- La estructura jerárquica permite máximo 2 niveles de profundidad.
